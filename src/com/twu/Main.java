package com.twu;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        ControlPanel myTrendingSystem = new ControlPanel();
        myTrendingSystem.welcomePage();
    }
}

class Person {
    private String personName;

    Person(String personName) {
        this.personName = personName;
    }

    String getPersonName() {
        return this.personName;
    }

    void checkTrending(TrendingList trendingList) {
        trendingList.checkTrending();
    }

    void addTrending(TrendingList trendingList, String trendingName) {
        trendingList.addTrending(trendingName);
    }
}

class User extends Person {
    private int leftVote;

    User(String personName) {
        super(personName);
        this.leftVote = 10;
    }

    void buyTrending(TrendingList trendingList, String trendingName, int money, int position) {
        trendingList.buyTrenging(trendingName, money, position);
    }

    void voteTrending(TrendingList trendingList, String trendingName, int voteCount) {
        if (voteCount > this.leftVote || voteCount < 1) {
            System.out.println("投票失败!");
            return;
        }
        if (trendingList.voteTrending(trendingName, voteCount))
            this.leftVote -= voteCount;
    }

    int getLeftVote() {
        return this.leftVote;
    }
}

class Admin extends Person {
    private String password;

    Admin(String personName, String password) {
        super(personName);
        this.password = password;
    }

    boolean verified(String password) {
        return this.password.equals(password);
    }

    void addSuperTrending(TrendingList trendingList, String trendingName) {
        if (!trendingList.containsTrending(trendingName)) {
            trendingList.addTrending(trendingName);
        } else System.out.println("该热搜已存在，已将其改为超级热搜！");
        trendingList.setSuperTrending(trendingName);
    }
}

class Trending implements Comparable<Trending> {
    private String trendingName;
    private int voteCount;
    private boolean isSuperTrending;
    private int boughtPosition;
    private int price;

    Trending(String trendingName) {
        this.trendingName = trendingName;
        this.voteCount = 0;
        this.isSuperTrending = false;
        this.boughtPosition = 0;
        this.price = 0;
    }

    void setSuperTrending() {
        this.isSuperTrending = true;
    }

    void cancelSuperTrending() {
        this.isSuperTrending = false;
    }

    int getVoteCount() {
        return this.voteCount;
    }

    void voteTrengding(int voteCount) {
        this.voteCount += this.isSuperTrending == true ? 2 * voteCount : voteCount;
        System.out.println("投票成功！");
    }

    boolean getIsSold() {
        return this.boughtPosition > 0;
    }

    int getBoughtPosition() {
        return boughtPosition;
    }

    void buyPosition(int money, int buyedPosition) {
        this.price = money;
        this.boughtPosition = buyedPosition;
    }

    int getPrice() {
        return this.price;
    }

    String getTrendingName() {
        return this.trendingName;
    }

    @Override
    public int compareTo(Trending otherTrending) {
        return otherTrending.getVoteCount() - this.voteCount;
    }
}

class TrendingList {
    private HashMap<String, Trending> trendingMap;
    private List<Trending> trendingList;

    TrendingList() {
        this.trendingList = new ArrayList<>();
        this.trendingMap = new HashMap<>();
    }

    void buyTrenging(String trendingName, int money, int position) {
        if (trendingList.get(position - 1).getPrice() >= money) {
            System.out.println("购买失败！");
            return;
        }
        if (!trendingMap.containsKey(trendingName)) {
            System.out.println("热搜不存在！");
            return;
        }
        if (trendingList.get(position - 1).getIsSold()) {
            trendingMap.remove(trendingList.get(position - 1));
            trendingList.remove(position - 1);
        }
        trendingMap.get(trendingName).buyPosition(money, position);
        sortTrendingList();
        System.out.println("购买成功！");
    }

    void sortTrendingList() {
        List<Trending> boughtTrendingList = trendingList.stream().filter(trending -> trending.getIsSold() == true).collect(Collectors.toList());
        Collections.sort(boughtTrendingList, new Comparator<Trending>() {
            @Override
            public int compare(Trending o1, Trending o2) {
                return o1.getBoughtPosition() - o2.getBoughtPosition();
            }
        });
        trendingList = trendingList.stream().filter(trending -> trending.getIsSold() == false).sorted().collect(Collectors.toList());
        //Collections.sort(this.trendingList);
        for (Trending trending : boughtTrendingList)
            trendingList.add(trending.getBoughtPosition() - 1, trending);
    }

    boolean voteTrending(String trendingName, int voteCount) {
        if (!trendingMap.containsKey(trendingName)) {
            System.out.println("热搜不存在！");
            return false;
        }
        trendingMap.get(trendingName).voteTrengding(voteCount);
        sortTrendingList();
        return true;
    }

    void addTrending(String trendingName) {
        if (this.trendingMap.containsKey(trendingName)) {
            System.out.println("热搜已存在！");
            return;
        }
        this.trendingMap.put(trendingName, new Trending(trendingName));
        this.trendingList.add(this.trendingMap.get(trendingName));
        System.out.println("添加成功！");
    }

    void checkTrending() {
        if (trendingList.size() == 0) {
            System.out.println("当前没有热搜！");
            return;
        }
        int trengingRanking = 1;
        for (Trending trending : trendingList) {
            System.out.println(trengingRanking + " " + trending.getTrendingName() + " " + trending.getVoteCount());
            trengingRanking++;
        }
    }

    boolean containsTrending(String trendingName) {
        return trendingMap.containsKey(trendingName);
    }

    void setSuperTrending(String trendingName) {
        this.trendingMap.get(trendingName).setSuperTrending();
    }

    int size() {
        return this.trendingList.size();
    }
}

class UserList {
    private HashMap<String, User> userList;

    UserList() {
        this.userList = new HashMap<>();
    }

    boolean containUser(String userName) {
        return userList.containsKey(userName);
    }

    void addUser(String userName) {
        userList.put(userName, new User(userName));
    }

    User userLogin(String userName) {
        if (!containUser(userName))
            addUser(userName);
        return userList.get(userName);
    }
}

class AdminList {
    private HashMap<String, Admin> adminList;

    AdminList() {
        this.adminList = new HashMap<>();
    }

    boolean containAdmin(String adminName) {
        return adminList.containsKey(adminName);
    }

    void addAdmin(String adminName, String adminPassword) {
        if (containAdmin(adminName)) {
            System.out.println("该管理员账户已存在！");
            return;
        }
        adminList.put(adminName, new Admin(adminName, adminPassword));
        return;
    }

    boolean adminLogin(String adminName, String adminPassword) {
        return adminList.get(adminName).verified(adminPassword);
    }

    Admin getAdmin(String adminName) {
        return adminList.get(adminName);
    }

}

class ControlPanel {
    private UserList userList;
    private AdminList adminList;
    private TrendingList trendingList;
    private Scanner in;

    ControlPanel() {
        this.adminList = new AdminList();
        this.userList = new UserList();
        this.trendingList = new TrendingList();
        this.in = new Scanner(System.in);
        adminList.addAdmin("admin", "admin123");
    }

    void welcomePage() {
        while (true) {
            System.out.println("欢迎来到热搜排行榜，您是？");
            System.out.println("1.用户");
            System.out.println("2.管理员");
            System.out.println("3.退出");
            String input = in.nextLine();
            if (input.equals("1")) {
                System.out.println("请输入您的昵称：");
                String userName = in.nextLine();
                User curUser = userList.userLogin(userName);
                userPage(curUser);
            } else if (input.equals("2")) {
                System.out.println("请输入您的昵称：");
                String adminName = in.nextLine();
                System.out.println("请输入您的密码：");
                String adminPassword = in.nextLine();
                if (adminList.adminLogin(adminName, adminPassword))
                    adminPage(adminList.getAdmin(adminName));
                else System.out.println("账号或密码错误！");
            } else if (input.equals("3")) {
                System.out.println("已退出热搜排行榜！");
                return;
            } else System.out.println("输入错误，请重新输入");
        }
    }

    void userPage(User curUser) {
        while (true) {
            System.out.println("您好，" + curUser.getPersonName() + "，您可以：");
            System.out.println("1.查看热搜排行榜");
            System.out.println("2.给热搜事件投票");
            System.out.println("3.购买热搜");
            System.out.println("4.添加热搜");
            System.out.println("5.退出");
            String input = in.nextLine();
            if (input.equals("1")) {
                curUser.checkTrending(trendingList);
            } else if (input.equals("2")) {
                userVote(curUser);
            } else if (input.equals("3")) {
                buyTrending();
            } else if (input.equals("4")) {
                addTrending();
            } else if (input.equals("5")) {
                return;
            } else System.out.println("输入错误，将返回用户界面！");
        }
    }

    void adminPage(Admin admin) {
        while (true) {
            System.out.println("您好，" + admin.getPersonName() + "，您可以：");
            System.out.println("1.查看热搜排行榜");
            System.out.println("2.添加热搜");
            System.out.println("3.添加超级热搜");
            System.out.println("4.退出");
            String input = in.nextLine();
            if (input.equals("1")) {
                admin.checkTrending(trendingList);
            } else if (input.equals("2")) {
                addTrending();
            } else if (input.equals("3")) {
                addSuperTrending(admin);
            } else if (input.equals("4")) {
                return;
            } else System.out.println("输入错误，将返回管理员界面！");
        }
    }

    void userVote(User user) {
        System.out.println("请输入您要投票的热搜名称：");
        String trending = in.nextLine().toLowerCase();
        System.out.println("请输入您要投票的热搜票数（您目前还有" + user.getLeftVote() + "票）：");
        String voteCountInput = in.nextLine();
        int voteCount;
        try {
            voteCount = Integer.parseInt(voteCountInput);
        } catch (NumberFormatException e) {
            System.out.println("输入错误，将返回用户界面！");
            return;
        }
        user.voteTrending(trendingList, trending, voteCount);
        return;
    }

    void buyTrending() {
        System.out.println("请输入您要购买的热搜名称：");
        String trending = in.nextLine().toLowerCase();
        System.out.println("请输入您要购买的热搜排名：");
        String positionInput = in.nextLine();
        System.out.println("请输入您要购买的热搜金额：");
        String moneyInput = in.nextLine();
        int position, money;
        try {
            position = Integer.parseInt(positionInput);
            money = Integer.parseInt(moneyInput);
        } catch (NumberFormatException e) {
            System.out.println("输入错误，将返回用户界面！");
            return;
        }
        if (position > trendingList.size() || position < 1) {
            System.out.println("输入错误，将返回用户界面！");
            return;
        }
        trendingList.buyTrenging(trending, money, position);
        return;
    }

    void addTrending() {
        System.out.println("请输入您要添加的热搜事件名字：");
        String trending = in.nextLine().toLowerCase();
        trendingList.addTrending(trending);
        return;
    }

    void addSuperTrending(Admin admin) {
        System.out.println("请输入您要添加的超级热搜事件名称：");
        String trending = in.nextLine().toLowerCase();
        admin.addSuperTrending(trendingList, trending);
        return;
    }
}