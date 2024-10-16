package TemaTest;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class Postare extends App implements Likeable {
    protected String[] text;

    public Postare() {}

    public Postare(String[] text) {
        this.text = text;
    }

    public static boolean checkId(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {   //go through the posts file
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[1].equals(id)) {    //check if the id exists there
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static boolean checkLiked(String id, String user) {
        try (BufferedReader br = new BufferedReader(new FileReader(likedPostsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(id)) {
                    if (data[1].equals(user))
                        return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static int countLikes(String id) {
        int likes = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(likedPostsPath))) {
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

    public static int countComments(String id) {
        int likes = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(commentsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[3].equals(id)) {
                    likes++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return likes;
    }

    private static String getLikes(String line) {  //found it online
        return line.split(",")[4];  //I use it while sorting, to specify the collumn by which I want to sort
    }                                     //the lines of the file

    private static String getComms(String line) {   //it does the same thing, but it has a suggestive name
        return line.split(",")[4];
    }

    public static void createPost(String[] strings) {
        String[] user;
        String[] pass;
        String[] text;
        LocalDateTime date = LocalDateTime.now();

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
            text = strings[3].split(" ");
            text[1] = text[1].replace("'", "");
            text[text.length - 1] = text[text.length - 1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No text provided'}");
            return;
        }

        int postLen = 0;

        for (int i = 0; i < text.length - 1; i++) {
            text[i] = text[i + 1];
            postLen += text[i].length();
        }

        text[text.length - 1] = "";

        if (postLen > 300) {
            System.out.println("{ 'status' : 'error', 'message' : 'Post text length exceeded'}");
            return;
        }

        postId++;   //counting total posts

        try (FileWriter fw = new FileWriter(postsPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(user[1] + ',' + postId + ',' + DateTimeFormatter.ofPattern("dd-MM-yyy").format(date) + ',');

            for (String s : text) {
                out.print(s + " ");
            }

            out.print("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Post added successfully'}");
    }

    public static void deletePost(String[] strings) {
        String[] user;
        String[] pass;
        String[] id;

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
            id = strings[3].split(" ");
            id[1] = id[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No identifier was provided'}");
            return;
        }

        if (checkId(id[1])) {   //see if the post exists
            System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1]) && data[1].equals(id[1])) {  //find and empty the file
                    FileWriter fw = new FileWriter(postsPath, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);

                    out.print("");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Post deleted successfully'}");
    }

    public static void likePost(String[] strings) {
        String[] user;
        String[] pass;
        String[] id;

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
            id = strings[3].split(" ");
            id[1] = id[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to like was provided'}");
            return;
        }

        //todo check if you follow the post's owner
        //didnt do it

        if (checkId(id[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }

        if (!checkLiked(id[1], user[1])) {  //check if the post isn't already liked
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }

        try (FileWriter fw = new FileWriter(likedPostsPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(id[1] + ',' + user[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unlikePost(String[] strings) {
        String[] user;
        String[] pass;
        String[] id;

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
            id = strings[3].split(" ");
            id[1] = id[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to unlike was provided'}");
            return;
        }

        if (checkId(id[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }

        if (checkLiked(id[1], user[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(likedPostsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1]) && data[1].equals(id[1])) {
                    FileWriter fw = new FileWriter(likedPostsPath, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);

                    out.print("");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void getDetails(String[] strings) {
        String[] user;
        String[] pass;
        String[] id;
        String[] postDate = null;
        int likesPost;
        String text = null;
        ArrayList<String> comments;

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
            id = strings[3].split(" ");
            id[1] = id[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier was provided'}");
            return;
        }

        if (checkId(id[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1])) {
                    if (data[1].equals(id[1])) {
                        text = data[3];
                        postDate = data[2].split(" ");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        likesPost = countLikes(id[1]);
        comments = Comentariu.getCommUser(id[1]);

        assert text != null;
        System.out.print("{'status' : 'ok', 'message' : [{'post_text' : '" + text.replaceAll("\\s+$", "") + "', 'post_date' :'" +
                    postDate[0] + "', 'username' : '" + user[1] + "', 'number_of_likes' :" +
                    " '" + likesPost + "', 'comments' : ");
        for (int i = 0; i < comments.size(); i++) {
            String[] data = comments.get(i).split(",");
            String[] commDate = data[2].split(" ");

            System.out.print("[{'comment_id' : '" + data[1] + "' ," +
                    " 'comment_text' : '" + data[4].replaceAll("\\s+$", "") + "', 'comment_date' : '" + commDate[0] + "', " +
                    "'username' : '" + data[0] + "', 'number_of_likes' : '" + Comentariu.countLikes(data[1]) + "'}]");
            if (i < comments.size() - 1) {
                System.out.print(",");
            }
        }

        System.out.print(" }] }");
    }

    public static void getMostLikedPosts(String[] strings) {
        String[] user;
        String[] pass;
        ArrayList<String> posts = new ArrayList<>();

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

        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                posts.add(line.replaceAll("\\s+$", "") + ',' + countLikes(data[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        posts.sort(Comparator.comparing(Postare::getLikes, Comparator.reverseOrder()));

        try (FileWriter fw = new FileWriter(topPostsPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String s : posts) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < posts.size() && i < 5; i++) {
            String[] data = posts.get(i).split(",");

            System.out.print("{'post_id' : '" + data[1] + "' ," +
                    " 'post_text' : '" + data[3].replaceAll("\\s+$", "") + "', 'post_date' : '" + data[2] + "', " +
                    "'username' : '" + data[0] + "', 'number_of_likes' : '" + Postare.countLikes(data[1]) + "'}");
            if (i < posts.size() - 1 && i < 4) {
                System.out.print(",");
            }
        }

        System.out.print(" ]}");
    }

    public static void getMostCommentedPosts(String[] strings) {
        String[] user;
        String[] pass;
        ArrayList<String> posts = new ArrayList<>();

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

        try (BufferedReader br = new BufferedReader(new FileReader(postsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                posts.add(line.replaceAll("\\s+$", "") + ',' + countComments(data[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        posts.sort(Comparator.comparing(Postare::getComms, Comparator.reverseOrder()));

        try (FileWriter fw = new FileWriter(topCommsPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (String s : posts) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("{ 'status' : 'ok', 'message' : [");

        for (int i = 0; i < posts.size() && i < 5; i++) {
            String[] data = posts.get(i).split(",");

            System.out.print("{'post_id' : '" + data[1] + "' ," +
                    " 'post_text' : '" + data[3].replaceAll("\\s+$", "") + "', 'post_date' : '" + data[2] + "', " +
                    "'username' : '" + data[0] + "', 'number_of_comments' : '" + data[4] + "'}");
            if (i < posts.size() - 1 && i < 4) {
                System.out.print(",");
            }
        }

        System.out.print("]}");
    }
}
