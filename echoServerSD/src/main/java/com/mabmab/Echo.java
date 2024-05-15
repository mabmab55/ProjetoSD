package com.mabmab;

import java.io.*;
import java.net.*;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.json.JSONObject;

public class Echo extends Thread {
    protected Socket clientSocket;
    private EntityManagerFactory entityManagerFactory;

    public Echo(Socket clientSoc, EntityManagerFactory entityManagerFactory) {
        clientSocket = clientSoc;
        this.entityManagerFactory = entityManagerFactory;
        start();
    }

    public void run() {
        System.out.println("New Communication Thread Started");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String jsonString;
            while ((jsonString = in.readLine()) != null) {
                System.out.println(jsonString);

                JSONObject jsonObject = new JSONObject(jsonString);
                String operacao = jsonObject.getString("operacao");
                //System.out.println(jsonObject);

                switch (operacao) {
                    case "cadastrarCandidato":
                        cadastrar(jsonObject, out);
                        break;
                    case "loginCandidato":
                        realizarLogin(jsonObject, out);
                        break;
                    case "visualizarCandidato":
                        buscarDados(jsonObject, out);
                        break;
                    case "apagarCandidato":
                        apagarCandidato(jsonObject, out);
                        break;
                    case "atualizarCandidato":
                        atualizarCandidato(jsonObject, out);
                        break;
                    case "logout":
                        logout(jsonObject, out);
                        break;       // Exit the thread after logout
                    default:
                        out.println("Invalid operation: " + operacao);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Close the connection after handling all operations
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarCandidato(JSONObject jsonObject, PrintWriter out) {
        // Get email, nome, and senha from the JSON object
        String email = jsonObject.getString("email");
        String nome = jsonObject.getString("nome");
        String senha = jsonObject.getString("senha");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Query to find the candidate by email
            TypedQuery<Candidato> query = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            query.setParameter("email", email);
            Candidato candidato = query.getSingleResult();

            // If candidate exists, update its information
            if (candidato != null) {
                candidato.setNome(nome);
                candidato.setSenha(senha);
                entityManager.merge(candidato);
                entityManager.getTransaction().commit();

                // Send success response to client
                JSONObject successJson = new JSONObject();
                successJson.put("operacao", "atualizarCandidato");
                successJson.put("status", 201);
                out.println(successJson.toString());
                System.out.println(successJson);
            } else {
                // Send error response to client if candidate not found
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "atualizarCandidato");
                errorJson.put("status", 404);
                errorJson.put("mensagem", "E-mail não encontrado");
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no candidate found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "atualizarCandidato");
            errorJson.put("status", 404);
            errorJson.put("mensagem", "E-mail não encontrado");
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            // Send error response to client if exception occurs
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "atualizarCandidato");
            errorJson.put("status", 500);
            errorJson.put("mensagem", "Erro interno do servidor");
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } finally {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void logout(JSONObject jsonObject, PrintWriter out) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("operacao", "logout");
        jsonResponse.put("status", 204);

        out.println(jsonResponse.toString());
        System.out.println(jsonResponse);
    }

    private void cadastrar(JSONObject jsonObject, PrintWriter out) {
        // Get user data from JSONObject
        String nome = jsonObject.getString("nome");
        String email = jsonObject.getString("email");
        String senha = jsonObject.getString("senha");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Check if a user with the same email already exists
            TypedQuery<Long> emailQuery = entityManager.createQuery("SELECT COUNT(c) FROM Candidato c WHERE c.email = :email", Long.class);
            emailQuery.setParameter("email", email);
            long count = emailQuery.getSingleResult();

            if (count > 0) {
                // If a user with the same email already exists, send an error response
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "cadastrarCandidato");
                errorJson.put("status", 422);
                errorJson.put("mensagem", "E-mail já cadastrado");
                out.println(errorJson.toString());
                System.out.println(errorJson);
                return; // Exit the method
            }

            // Save user and password to the database if email is not already registered
            entityManager.getTransaction().begin();

            // Use the constructor to create a new Candidato instance
            Candidato candidato = new Candidato(nome, email, senha);
            entityManager.persist(candidato);

            entityManager.getTransaction().commit();

            // Generate a unique token
            String token = UUID.randomUUID().toString();

            // Create a JSON object with the response data
            JSONObject responseJson = new JSONObject();
            responseJson.put("operacao", "cadastrarCandidato");
            responseJson.put("status", 201);
            responseJson.put("token", token);

            // Send the JSON response to the client
            out.println(responseJson.toString());
            System.out.println(responseJson);

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
            // Send an error response to the client if an exception occurs
            out.println("Failed to register user.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void realizarLogin(JSONObject jsonObject, PrintWriter out) {
        // Get user data from JSONObject
        String email = jsonObject.getString("email");
        String senha = jsonObject.getString("senha");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Query to find the user by email
            TypedQuery<Candidato> query = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            query.setParameter("email", email);
            Candidato candidato = query.getSingleResult();

            // Check if the user exists and if the password matches
            if (candidato != null && candidato.getSenha().equals(senha)) {
                // Generate a unique token
                String token = UUID.randomUUID().toString();

                // Create a JSON object with the response data
                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "loginCandidato");
                responseJson.put("status", 200);
                responseJson.put("token", token);

                // Send the JSON response to the client
                out.println(responseJson.toString());
                System.out.println(responseJson);
            } else {
                // Create a JSON object with the error message
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "loginCandidato");
                errorJson.put("status", 401);
                errorJson.put("mensagem", "Login ou senha incorretos");

                // Send the JSON response to the client
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no user is found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "loginCandidato");
            errorJson.put("status", 401);
            errorJson.put("mensagem", "Login ou senha incorretos");

            // Send the JSON response to the client
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("An error occurred during login.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void buscarDados(JSONObject jsonObject, PrintWriter out) {
        // Get email from the JSON object
        String email = jsonObject.getString("email");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Query to find the candidate by email
            TypedQuery<Candidato> query = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            query.setParameter("email", email);
            Candidato candidato = query.getSingleResult();

            // Check if the candidate exists
            if (candidato != null) {
                // Create a JSON object with the candidate data
                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "visualizarCandidato");
                responseJson.put("status", 201);
                responseJson.put("nome", candidato.getNome());
                responseJson.put("senha", candidato.getSenha());

                // Send the JSON response to the client
                out.println(responseJson.toString());
                System.out.println(responseJson);
            } else {
                // Create a JSON object with an error message if the candidate is not found
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "visualizarCandidato");
                errorJson.put("status", 404); // Not found status
                errorJson.put("mensagem", "E-mail não encontrado");

                // Send the JSON response to the client
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no candidate is found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "visualizarCandidato");
            errorJson.put("status", 404);
            errorJson.put("mensagem", "E-mail não encontrado");

            // Send the JSON response to the client
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Erro ao buscar dados do candidato.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void apagarCandidato(JSONObject jsonObject, PrintWriter out) {
        // Get email from JSONObject
        String email = jsonObject.getString("email");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Find the candidate by email
            TypedQuery<Candidato> query = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            query.setParameter("email", email);
            Candidato candidato = query.getSingleResult();

            // If candidate exists, remove it from the database
            if (candidato != null) {
                entityManager.remove(candidato);
                entityManager.getTransaction().commit();

                // Send success response to client
                JSONObject successJson = new JSONObject();
                successJson.put("operacao", "apagarCandidato");
                successJson.put("status", 201);
                System.out.println(successJson);
                out.println(successJson.toString());
            } else {
                // Send error response to client if candidate not found
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "apagarCandidato");
                errorJson.put("status", 404);
                errorJson.put("mensagem", "E-mail não encontrado");
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no candidate found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "apagarCandidato");
            errorJson.put("status", 404);
            errorJson.put("mensagem", "E-mail não encontrado");
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            // Send error response to client if exception occurs
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "apagarCandidato");
            errorJson.put("status", 500);
            errorJson.put("mensagem", "Erro interno do servidor");
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } finally {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        EntityManagerFactory entityManagerFactory = EntityManagerFactorySingleton.getEntityManagerFactory();

        try {
            serverSocket = new ServerSocket(22222);
            System.out.println("Connection Socket Created");
            try {
                while (true) {
                    System.out.println("Waiting for Connection");
                    new Echo(serverSocket.accept(), entityManagerFactory);
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 21000.");
            System.exit(1);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Could not close port: 21000.");
                System.exit(1);
            }
        }
    }
}