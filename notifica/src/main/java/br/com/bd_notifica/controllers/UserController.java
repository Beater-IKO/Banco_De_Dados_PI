package br.com.bd_notifica.controllers;

import java.time.LocalDate;
import java.util.Scanner;

import br.com.bd_notifica.entities.UserEntity;
import br.com.bd_notifica.enums.UserRole;
import br.com.bd_notifica.repositories.TicketRepository;
import br.com.bd_notifica.repositories.UserRepository;
import br.com.bd_notifica.services.TicketService;
import br.com.bd_notifica.services.UserService;
import br.com.bd_notifica.utils.Criptografia;

public class UserController {
    static Scanner input = new Scanner(System.in);

    public static void login() {
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserEntity userLogado = new UserEntity();
        UserEntity user = null;

        userService.criarUserPadrão();

        int op;
        do {
            System.out.println("1 - Login");
            System.out.println("2 - Registro");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            op = input.nextInt();
            input.nextLine();

            switch (op) {
                case 1 -> {
                    System.out.print("Insira seu email: ");
                    String email = input.nextLine();
                    System.out.print("Insira sua senha: ");
                    String password = input.nextLine();

                    user = userService.buscarPorEmail(email);

                    if (user != null && Criptografia.verificarSenha(password, user.getPassword())) {
                        userLogado = user;

                        System.out.println("Login bem-sucedido como: " + user.getRole());

                        if (user.getRole() == UserRole.ADMIN) {
                            TicketService service = new TicketService(new TicketRepository());
                            AdminTicketController.menuAdm(service, userService, userLogado);
                        } else if (user.getRole() == UserRole.STUDENT) {
                            TicketService service = new TicketService(new TicketRepository());
                            AlunoController.menuAluno(userService, service, user);
                        } else if (user.getRole() == UserRole.AGENT) {
                            AgenteDeCampo.executarMenuAgente();
                        } else {
                            System.out.println("Tipo de usuário inválido!");
                        }

                    } else {
                        System.out.println("Email ou senha incorretos.");
                    }
                }

                case 2 -> {
                    System.out.print("Insira seu nome: ");
                    String name = input.nextLine();
                    System.out.print("Insira seu email: ");
                    String email = input.nextLine();
                    System.out.print("Insira sua senha: ");
                    String password = input.nextLine();
                    System.out.println("Insira seu tipo de usuário: ");
                    System.out.println("1 - ADMIN");
                    System.out.println("2 - ALUNO");
                    System.out.println("3 - AGENTE");
                    int tipo = input.nextInt();
                    input.nextLine();

                    UserRole role = switch (tipo) {
                        case 1 -> UserRole.ADMIN;
                        case 2 -> UserRole.STUDENT;
                        case 3 -> UserRole.AGENT;
                        default -> null;
                    };

                    if (role != null) {
                        user = new UserEntity();
                        user.setName(name);
                        user.setEmail(email);
                        user.setPassword(Criptografia.gerarHash(password));  
                        user.setRole(role);
                        user.setCreateOnDate(LocalDate.now());

                        userRepository.createUser(user);
                        System.out.println("Usuário criado com sucesso!");
                    } else {
                        System.out.println("Tipo de usuário inválido!");
                    }
                }

                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }

        } while (op != 0);
    }
}
