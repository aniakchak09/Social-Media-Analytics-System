package TemaTest;

import java.io.*;
import java.util.*;

public class Utilizator extends App {
    protected String username; //should it be other than protected?
    protected String password;
    public Utilizator() {}

    public Utilizator(String username, String password) {   //it took me a few hours to figure that this won't be used ever
        this.username = username;
        this.password = password;
    }

    public static boolean isLogged(String username, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(username)) {
                    if (data[1].equals(pass))
                        return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
        return true;
    }

    public static boolean userExists(String user) {
        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    public static boolean isFollowed(String username, String user) {
        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(username)) {
                    if (data[1].equals(user))
                        return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private static String getFollowersToSort(String line) {  //not sure I need it
        return line.split(",")[2];
    }

    private static int countFollowers(String user) {
        int likes = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[1].equals(user)) {
                    likes++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return likes;
    }

    public static int countCommLikes(String id) {
        int likes = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(likedCommPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(id)) {
                    likes++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return likes;
    }

    public static void createUser(String[] strings){
        String[] user;
        String[] pass;

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide username'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'Please provide password'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1])) {
                    System.out.println("{ 'status' : 'error', 'message' : 'User already exists'}");
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fw = new FileWriter(usersPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(user[1] + ',' + pass[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("{ 'status' : 'ok', 'message' : 'User created successfully'}");
    }

    public static void follow(String[] strings) {
        String[] user;
        String[] pass;
        String[] follower;

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (Utilizator.isLogged(user[1], pass[1])) {
            return;
        }

        if (strings.length >= 4) {
            follower = strings[3].split(" ");
            follower[1] = follower[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to follow was provided'}");
            return;
        }

        if (userExists(follower[1]) || isFollowed(user[1], follower[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to follow was not valid'}");
            return;
        }

        try (FileWriter fw = new FileWriter(followingPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(user[1] + ',' + follower[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unfollow(String[] strings) {
        String[] user;
        String[] pass;
        String[] follower;
        boolean found = false;
        boolean following = false;

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (Utilizator.isLogged(user[1], pass[1])) {
            return;
        }

        if (strings.length >= 4) {
            follower = strings[3].split(" ");
            follower[1] = follower[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to unfollow was provided'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {   //check if follower exists
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(follower[1])) {
                    found = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {   //check already following
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(user[1]) && data[1].equals(follower[1])) {
                    following = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (found && following) {
            try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    if(data[1].equals(follower[1])) {
                        FileWriter fw = new FileWriter(followingPath, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw);

                        out.print("");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to unfollow was not valid'}");
        }
    }

    public static void getFollowingPosts(String[] strings) {
        String[] user;
        String[] pass;

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (isLogged(user[1], pass[1])) {
            return;
        }

        ArrayList<String> posts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                try (BufferedReader br_1 = new BufferedReader(new FileReader(postsPath))) {
                    String line_1;

                    while ((line_1 = br_1.readLine()) != null) {
                        String[] data_1 = line_1.split(",");

                        if (data[1].equals(data_1[0])) {
                            posts.add(line_1);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            posts.sort(Collections.reverseOrder());

            try (FileWriter fw = new FileWriter(sortFollowingPostsPath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                for (String s : posts) {
                    out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < posts.size(); i++) {
            String[] data = posts.get(i).split(",");

            String[] date = data[2].split(" ");

            System.out.print("{'post_id' : '" + data[1] + "', 'post_text' : '" + data[3].replaceAll("\\s+$", "") + "', 'post_date' : '" + date[0] + "', 'username' : '" + data[0] + "'}");

            if (i < posts.size() - 1) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }

    public static void getUserPosts(String[] strings) {
        String[] user;
        String[] pass;
        String[] following;

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (isLogged(user[1], pass[1])) {
            return;
        }

        if (strings.length >= 4) {
            following = strings[3].split(" ");
            following[1] = following[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to list posts was provided'}");
            return;
        }

        if (!isFollowed(user[1], following[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list posts was not valid'}");
            return;
        }

        ArrayList<String> posts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(following[1])) {
                    posts.add(line);
                }
            }

            posts.sort(Collections.reverseOrder());

            try (FileWriter fw = new FileWriter(sortUserPostsPath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                for (String s : posts) {
                    out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < posts.size(); i++) {
            String[] data = posts.get(i).split(",");

            String[] date = data[2].split(" ");

            System.out.print("{'post_id' : '" + data[1] + "', 'post_text' : '" + data[3].replaceAll("\\s+$", "") + "', 'post_date' : '" + date[0] + "'}");

            if (i < posts.size() - 1) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }

    public static void getFollowing(String[] strings) {
        String[] user;
        String[] pass;
        ArrayList<String> following = new ArrayList<>();

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (isLogged(user[1], pass[1])) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(user[1])) {
                    following.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("{ 'status' : 'ok', 'message' : [ ");

        for (int i = 0; i < following.size(); i++) {
            String[] data = following.get(i).split(",");

            System.out.print("'" + data[1] + "'");

            if (i < following.size() - 1) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }

    public static void getFollowers(String[] strings) {
        String[] user;
        String[] pass;
        String[] stalk;
        ArrayList<String> followers = new ArrayList<>();

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (isLogged(user[1], pass[1])) {
            return;
        }

        if (strings.length >= 4) {
            stalk = strings[3].split(" ");
            stalk[1] = stalk[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to list followers was provided'}");
            return;
        }

        if (userExists(stalk[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list followers was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(followingPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[1].equals(stalk[1])) {
                    followers.add(line);
                }
            }

            try (FileWriter fw = new FileWriter(followersPath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                for (String s : followers) {
                    out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("{ 'status' : 'ok', 'message' : [ ");

        for (int i = 0; i < followers.size(); i++) {
            String[] data = followers.get(i).split(",");

            System.out.print("'" + data[0] + "'");

            if (i < followers.size() - 1) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }

    public static void getMostFollowed(String[] strings) {
        String[] user;
        String[] pass;
        ArrayList<String> users = new ArrayList<>();

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (Utilizator.isLogged(user[1], pass[1])) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                users.add(line + ',' + countFollowers(data[0]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        users.sort(Comparator.comparing(Utilizator::getFollowersToSort, Comparator.reverseOrder()));

        try (FileWriter fw = new FileWriter(topUsersPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String s : users) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < users.size() && i < 5; i++) {
            String[] data = users.get(i).split(",");

            System.out.print("{'username' : '" + data[0] + "' ," +
                    " 'number_of_followers' : '" + data[2] + "'}");
            if (i < users.size() - 1 && i < 4) {
                System.out.print(",");
            }
        }

        System.out.print(" ]}");
    }

    public static void getMostLikedUsers(String[] strings) {
        String[] user;
        String[] pass;
        int likes = 0;
        ArrayList<String> users = new ArrayList<>();

        if (strings.length >= 2) {
            user = strings[1].split(" ");
            user[1] = user[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (strings.length >= 3) {
            pass = strings[2].split(" ");
            pass[1] = pass[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        if (Utilizator.isLogged(user[1], pass[1])) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(usersPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                try (BufferedReader br_1 = new BufferedReader(new FileReader(postsPath))) {
                    String line_1;

                    while ((line_1 = br_1.readLine()) != null) {
                        String[] data_1 = line_1.split(",");

                        if (data_1[0].equals(data[0])) {
                            likes += Postare.countLikes(data_1[1]);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedReader br_1 = new BufferedReader(new FileReader(commentsPath))) {
                    String line_1;

                    while ((line_1 = br_1.readLine()) != null) {
                        String[] data_1 = line_1.split(",");

                        if (data_1[0].equals(data[0])) {
                            likes += countCommLikes(data_1[1]);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                users.add(line + ',' + likes);
                likes = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        users.sort(Comparator.comparing(Utilizator::getFollowersToSort, Comparator.reverseOrder()));

        try (FileWriter fw = new FileWriter(topLikedUsersPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String s : users) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < users.size() && i < 5; i++) {
            String[] data = users.get(i).split(",");

            System.out.print("{'username' : '" + data[0] + "' ," +
                    " 'number_of_likes' : '" + data[2] + "'}");
            if (i < users.size() - 1 && i < 4) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }
}
