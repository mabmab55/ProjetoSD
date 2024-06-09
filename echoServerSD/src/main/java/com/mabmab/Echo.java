package com.mabmab;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.json.JSONArray;
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
                System.out.println("recebi:" + jsonString);

                JSONObject jsonObject = new JSONObject(jsonString);
                String operacao = jsonObject.getString("operacao");
                //System.out.println(jsonObject);

                switch (operacao) {
                    case "cadastrarCandidato":
                        cadastrar(jsonObject, out);
                        break;
                    case "cadastrarEmpresa":
                        cadastrarEmpresa(jsonObject, out);
                        break;
                    case "loginCandidato":
                        realizarLogin(jsonObject, out);
                        break;
                    case "loginEmpresa":
                        realizarLoginEmpresa(jsonObject, out);
                        break;
                    case "visualizarCandidato":
                        buscarDados(jsonObject, out);
                        break;
                    case "visualizarEmpresa":
                        buscarDadosEmpresa(jsonObject, out);
                        break;
                    case "apagarCandidato":
                        apagarCandidato(jsonObject, out);
                        break;
                    case "apagarEmpresa":
                        apagarEmpresa(jsonObject, out);
                        break;
                    case "atualizarCandidato":
                        atualizarCandidato(jsonObject, out);
                        break;
                    case "atualizarEmpresa":
                        atualizarEmpresa(jsonObject, out);
                        break;
                    case "logout":
                        logout(jsonObject, out);
                        break;       // Exit the thread after logout
                    case "cadastrarCompetenciaExperiencia":
                        cadastrarCompetenciaExperiencia(jsonObject, out);
                        break;
                    case "visualizarCompetenciaExperiencia":
                        visualizarCompetenciaExperiencia(jsonObject, out);
                        break;
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

    private void visualizarCompetenciaExperiencia(JSONObject jsonObject, PrintWriter out) {
        String email = jsonObject.getString("email");
        String token = jsonObject.getString("token");
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Find the candidate by email
            TypedQuery<Candidato> queryCandidato = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            queryCandidato.setParameter("email", email);
            Candidato candidato = queryCandidato.getSingleResult();

            if (candidato == null) {
                sendErrorResponse(out, "visualizarCompetenciaExperiencia", 404, "Candidato não encontrado");
                return;
            }

            // Query to find all CompetenciaExperiencia for the given candidate
            TypedQuery<CompetenciaExperiencia> queryCompExp = entityManager.createQuery(
                    "SELECT ce FROM CompetenciaExperiencia ce WHERE ce.candidato.email = :email", CompetenciaExperiencia.class);
            queryCompExp.setParameter("email", email);
            List<CompetenciaExperiencia> competenciasExperiencias = queryCompExp.getResultList();

            // Build the JSON response
            JSONObject responseJson = new JSONObject();
            responseJson.put("operacao", "visualizarCompetenciaExperiencia");
            responseJson.put("status", 201);
            JSONArray compExpArray = new JSONArray();

            for (CompetenciaExperiencia ce : competenciasExperiencias) {
                JSONObject compExpJson = new JSONObject();
                compExpJson.put("competencia", ce.getCompetencia().getNome());
                compExpJson.put("experiencia", ce.getExperiencia());
                compExpArray.put(compExpJson);
            }

            responseJson.put("competenciaExperiencia", compExpArray);

            // Send the JSON response to the client
            out.println(responseJson.toString());
            System.out.println(responseJson + " enviado ");

        } catch (NoResultException e) {
            sendErrorResponse(out, "visualizarCompetenciaExperiencia", 404, "Candidato não encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "visualizarCompetenciaExperiencia", 500, "Erro ao buscar competências e experiências.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }


    private void cadastrarCompetenciaExperiencia(JSONObject jsonObject, PrintWriter out) {
        String email = jsonObject.getString("email");
        String token = jsonObject.getString("token");
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Find the candidate by email
            TypedQuery<Candidato> queryCandidato = entityManager.createQuery("SELECT c FROM Candidato c WHERE c.email = :email", Candidato.class);
            queryCandidato.setParameter("email", email);
            Candidato candidato = queryCandidato.getSingleResult();

            if (candidato == null) {
                sendErrorResponse(out, "cadastrarCompetenciaExperiencia", 404, "Candidato não encontrado");
                return;
            }

            // Parse and save each CompetenciaExperiencia
            for (Object obj : jsonObject.getJSONArray("competenciaExperiencia")) {
                JSONObject compExpJson = (JSONObject) obj;
                String competenciaNome = compExpJson.getString("competencia");
                int experiencia = compExpJson.getInt("experiencia");

                // Find or create Competencia
                Competencia competencia = entityManager.find(Competencia.class, competenciaNome);
                if (competencia == null) {
                    competencia = new Competencia(competenciaNome);
                    entityManager.persist(competencia);
                }

                // Create and persist CompetenciaExperiencia
                CompetenciaExperiencia compExp = new CompetenciaExperiencia(candidato, competencia, experiencia);
                entityManager.persist(compExp);
            }

            entityManager.getTransaction().commit();

            // Send success response
            JSONObject responseJson = new JSONObject();
            responseJson.put("operacao", "cadastrarCompetenciaExperiencia");
            responseJson.put("status", 201);
            out.println(responseJson.toString());
            System.out.println(responseJson);

        } catch (NoResultException e) {
            sendErrorResponse(out, "cadastrarCompetenciaExperiencia", 404, "Candidato não encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "cadastrarCompetenciaExperiencia", 500, "Erro ao cadastrar competências e experiências.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void atualizarEmpresa(JSONObject jsonObject, PrintWriter out) {
        String email = jsonObject.getString("email");
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            TypedQuery<Empresa> query = entityManager.createQuery("SELECT e FROM Empresa e WHERE e.email = :email", Empresa.class);
            query.setParameter("email", email);
            Empresa empresa = query.getSingleResult();

            if (empresa != null) {
                empresa.setRazaoSocial(jsonObject.getString("razaoSocial"));
                empresa.setCnpj(jsonObject.getString("cnpj"));
                empresa.setSenha(jsonObject.getString("senha"));
                empresa.setDescricao(jsonObject.getString("descricao"));
                empresa.setRamo(jsonObject.getString("ramo"));

                entityManager.getTransaction().commit();

                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "atualizarEmpresa");
                responseJson.put("status", 201);
                out.println(responseJson.toString());
            } else {
                sendErrorResponse(out, "atualizarEmpresa", 404, "E-mail não encontrado");
            }
        } catch (NoResultException e) {
            sendErrorResponse(out, "atualizarEmpresa", 404, "E-mail não encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "atualizarEmpresa", 500, "Erro ao atualizar dados da empresa.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void apagarEmpresa(JSONObject jsonObject, PrintWriter out) {
        String email = jsonObject.getString("email");
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            TypedQuery<Empresa> query = entityManager.createQuery("SELECT e FROM Empresa e WHERE e.email = :email", Empresa.class);
            query.setParameter("email", email);
            Empresa empresa = query.getSingleResult();

            if (empresa != null) {
                entityManager.remove(empresa);
                entityManager.getTransaction().commit();

                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "apagarEmpresa");
                responseJson.put("status", 201);
                out.println(responseJson.toString());
            } else {
                sendErrorResponse(out, "apagarEmpresa", 404, "E-mail não encontrado");
            }
        } catch (NoResultException e) {
            sendErrorResponse(out, "apagarEmpresa", 404, "E-mail não encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "apagarEmpresa", 500, "Erro ao apagar a empresa.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void sendErrorResponse(PrintWriter out, String operacao, int status, String mensagem) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("operacao", operacao);
        errorJson.put("status", status);
        errorJson.put("mensagem", mensagem);
        out.println(errorJson.toString());
    }


    private void buscarDadosEmpresa(JSONObject jsonObject, PrintWriter out) {
        // Get email from the JSON object
        String email = jsonObject.getString("email");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Query to find the company by email
            TypedQuery<Empresa> query = entityManager.createQuery("SELECT e FROM Empresa e WHERE e.email = :email", Empresa.class);
            query.setParameter("email", email);
            Empresa empresa = query.getSingleResult();

            // Check if the company exists
            if (empresa != null) {
                // Create a JSON object with the company data
                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "visualizarEmpresa");
                responseJson.put("status", 201);
                responseJson.put("razaoSocial", empresa.getRazaoSocial());
                responseJson.put("cnpj", empresa.getCnpj());
                responseJson.put("descricao", empresa.getDescricao());
                responseJson.put("ramo", empresa.getRamo());
                responseJson.put("senha", empresa.getSenha());

                // Send the JSON response to the client
                out.println(responseJson.toString());
                System.out.println(responseJson + " enviado ");
            } else {
                // Create a JSON object with an error message if the company is not found
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "visualizarEmpresa");
                errorJson.put("status", 404); // Not found status
                errorJson.put("mensagem", "E-mail da empresa não encontrado");

                // Send the JSON response to the client
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no company is found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "visualizarEmpresa");
            errorJson.put("status", 404);
            errorJson.put("mensagem", "E-mail da empresa não encontrado");

            // Send the JSON response to the client
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Erro ao buscar dados da empresa.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void realizarLoginEmpresa(JSONObject jsonObject, PrintWriter out) {
        // Get company data from JSONObject
        String email = jsonObject.getString("email");
        String senha = jsonObject.getString("senha");

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Query to find the company by email
            TypedQuery<Empresa> query = entityManager.createQuery("SELECT e FROM Empresa e WHERE e.email = :email", Empresa.class);
            query.setParameter("email", email);
            Empresa empresa = query.getSingleResult();

            // Check if the company exists and if the password matches
            if (empresa != null && empresa.getSenha().equals(senha)) {
                // Generate a unique token
                String token = UUID.randomUUID().toString();

                // Create a JSON object with the response data
                JSONObject responseJson = new JSONObject();
                responseJson.put("operacao", "loginEmpresa");
                responseJson.put("status", 200);
                responseJson.put("token", token);

                // Send the JSON response to the client
                out.println(responseJson.toString());
                System.out.println(responseJson);
            } else {
                // Create a JSON object with the error message
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "loginEmpresa");
                errorJson.put("status", 401);
                errorJson.put("mensagem", "Login ou senha incorretos");

                // Send the JSON response to the client
                out.println(errorJson.toString());
                System.out.println(errorJson);
            }
        } catch (NoResultException e) {
            // If no company is found for the given email
            JSONObject errorJson = new JSONObject();
            errorJson.put("operacao", "loginEmpresa");
            errorJson.put("status", 401);
            errorJson.put("mensagem", "Login ou senha incorretos");

            // Send the JSON response to the client
            out.println(errorJson.toString());
            System.out.println(errorJson);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("An error occurred during company login.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }


    private void cadastrarEmpresa(JSONObject jsonObject, PrintWriter out) {
        // Get user data from JSONObject
        String razaoSocial = jsonObject.getString("razaoSocial");
        String email = jsonObject.getString("email");
        String cnpj = jsonObject.getString("cnpj");
        String senha = jsonObject.getString("senha");
        String descricao = jsonObject.optString("descricao", ""); // Optional field
        String ramo = jsonObject.optString("ramo", ""); // Optional field

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Check if a company with the same email already exists
            TypedQuery<Long> emailQuery = entityManager.createQuery("SELECT COUNT(e) FROM Empresa e WHERE e.email = :email", Long.class);
            emailQuery.setParameter("email", email);
            long emailCount = emailQuery.getSingleResult();

            // Check if a company with the same CNPJ already exists
            TypedQuery<Long> cnpjQuery = entityManager.createQuery("SELECT COUNT(e) FROM Empresa e WHERE e.cnpj = :cnpj", Long.class);
            cnpjQuery.setParameter("cnpj", cnpj);
            long cnpjCount = cnpjQuery.getSingleResult();

            if (emailCount > 0 || cnpjCount > 0) {
                // If a company with the same email or CNPJ already exists, send an error response
                JSONObject errorJson = new JSONObject();
                errorJson.put("operacao", "cadastrarEmpresa");
                errorJson.put("status", 422);
                errorJson.put("mensagem", "E-mail ou CNPJ já cadastrados");
                out.println(errorJson.toString());
                System.out.println(errorJson);
                return; // Exit the method
            }

            // Save the company to the database if email and CNPJ are not already registered
            entityManager.getTransaction().begin();

            // Use the constructor to create a new Empresa instance
            Empresa empresa = new Empresa(razaoSocial, email, cnpj, senha, descricao, ramo);
            entityManager.persist(empresa);

            entityManager.getTransaction().commit();

            // Verify that the empresa was saved
            Empresa savedEmpresa = entityManager.find(Empresa.class, empresa.getId());
            if (savedEmpresa != null) {
                System.out.println("Empresa cadastrada com sucesso: " + savedEmpresa);
            } else {
                System.out.println("Falha ao cadastrar a empresa.");
            }

            // Generate a unique token
            String token = UUID.randomUUID().toString();

            // Create a JSON object with the response data
            JSONObject responseJson = new JSONObject();
            responseJson.put("operacao", "cadastrarEmpresa");
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
            out.println("Failed to register company.");
        } finally {
            if (entityManager != null) {
                entityManager.close();
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
                System.out.println(responseJson + " enviado ");
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