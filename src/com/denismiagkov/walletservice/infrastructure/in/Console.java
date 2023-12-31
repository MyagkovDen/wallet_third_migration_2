package com.denismiagkov.walletservice.infrastructure.in;

import com.denismiagkov.walletservice.application.controller.Controller;
import com.denismiagkov.walletservice.infrastructure.in.menu.MainMenu;
import com.denismiagkov.walletservice.infrastructure.in.menu.ProfileMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

/**
 * Класс реализует непосредственное взаимодействие приложения с пользователем через терминал,
 * принимая и обрабатывая введенные пользователем данные, возвращая пользователю информацию о результатах
 * выполнения его запросов.
 * */
public class Console implements View {

    /**
     * Котроллер
     * */
    private Controller controller;

    /***
     * BufferedReader считывает входящий поток данных
     * */
    private BufferedReader reader;

    /**
     * Статус рабочего состояния приложения
     * */
    private boolean work;

    /**
     * Стартовое меню
     * */
    private MainMenu mainMenu;

    /**
     * Меню приложения после авторизации игрока
     * */
    private ProfileMenu profileMenu;


    /**
     * Конструктор класса
     * */
    public Console(Controller controller) {
        this.controller = controller;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.work = true;
        this.mainMenu = new MainMenu(this);
        this.profileMenu = new ProfileMenu(this);
    }

    /**
     * Метод выводит в терминал принимаемый в качестве параметра текст.
     *
     * @param text информация, предназначенная для вывода в терминале
     * */
    @Override
    public void print(String text) {
        System.out.println(text);
    }


    /**
     * Метод выводит в терминал стартовое меню при запуске приложения, принимает и обрабатывает
     * введенные пользователем символы
     * */
    @Override
    public void start() {
        while (work) {
            System.out.println("Выберите действие: ");
            System.out.println(mainMenu.print());
            String choice = null;
            try {
                choice = reader.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());;
            }
            if (check(choice, mainMenu.getSize())) {
                mainMenu.execute(Integer.parseInt(choice));
            } else {
                fail();
            }
        }
    }

    /**
     * Метод выводит в терминал меню возможных действий игрока после его авторизации, принимает и обрабатывает
     * введенные пользователем данные
     * */
    @Override
    public void startProfile(String login, String password) {
        while (work) {
            System.out.println("Выберите действие: ");
            System.out.println(profileMenu.print());
            String choice = null;
            try {
                choice = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (check(choice, profileMenu.getSize())) {
                profileMenu.execute(Integer.parseInt(choice), login, password);
            } else {
                fail();
            }
        }
    }

    /**
     * Метод проверяет корректность введенных пользователем символов выбора команды в меню
     * */
    private boolean check(String text, int n) {
        return text.matches("[0-9]+") && Integer.parseInt(text) <= n && Integer.parseInt(text) > 0;
    }

    /**
     * Метод сообщает пользователю об ошибке ври вводе данных в терминал
     * */
    public void fail() {
        System.out.println("Ошибка ввода!");
    }

    /**
     * Метод завершает работу приложения
     * */
    public void finish(String login, String password) {
        System.out.println("Работа программы завершена.");
        controller.logExit(login, password);
        work = false;
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод принимает введенные пользователем данные при регистрации нового игрока и передает их в контроллер
     * */
    public void getDataToRegisterPlayer() {
        try {
            String[] input = new String[5];
            System.out.println("Введите имя: ");
            String firstName = reader.readLine();
            System.out.println("Введите фамилию: ");
            String lastName = reader.readLine();
            System.out.println("Введите адрес электронной почты: ");
            String email = reader.readLine();
            System.out.println("Введите логин: ");
            String login = reader.readLine();
            System.out.println("Введите пароль: ");
            String password = reader.readLine();
            controller.registerPlayer(firstName, lastName, email, login, password);
            System.out.println("Поздравляю! Вы успешно зарегистрированы!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод принимает введенные пользователем данные при аторизации игрока и передает их в контроллер
     * */
    public void getDataToAuthorizePlayer() {
        try {
            System.out.println("Введите логин: ");
            String login = reader.readLine();
            System.out.println("Введите пароль: ");
            String password = reader.readLine();
            if (controller.authorizePlayer(login, password)) {
                startProfile(login, password);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод запрашивает в контроллере баланс счета игрока и выводит результат запроса в терминал
     * */
    public void showCurrentBalance(String login, String password) {
        BigDecimal balance = controller.getCurrentBalance(login, password);
        System.out.println("Ваш текущий баланс составляет: " + balance + " денежных единиц");
    }

    /**
     * Метод передает в контроллер запрос на пополнение счета игрока и выводит полученный результат в терминал
     * */
    public void callTopUpAccount(String login, String password) {
        try {
            System.out.println("Введите уникальный идентификатор длиной до 6 символов");
            String uniqueId = reader.readLine();
            System.out.println("Введите сумму пополнения: ");
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
            boolean success = controller.topUpAccount(login, password, uniqueId, amount);
            if (success) {
                System.out.println("Ваш счет пополнен на сумму: " + amount + " денежных единиц\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Метод передает в контроллер запрос на списание средств со счета игрока
     * и выводит полученный результат в терминал
     * */
    public void callWriteOffFunds(String login, String password) {
        try {
            System.out.println("Введите уникальный идентификатор длиной до 6 символов");
            String uniqueId = reader.readLine();
            System.out.println("Введите сумму списания: ");
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
            boolean success = controller.writeOffFunds(login, password, uniqueId, amount);
            if (success) {
                System.out.println("С вашего счета списано: " + amount + " денежных единиц\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод запрашивает в контроллере историю транзакций и выводит полученный результат в терминал
     * */
    public void showTransactionHistory(String login, String password) {
        String transactionHistory = controller.getTransactionsHistory(login, password);
        System.out.println("История транзакций по Вашему счету: " + transactionHistory);
    }
}
