package TemaTest;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class Comentariu extends Postare implements Likeable {
    protected String[] comm;

    public Comentariu() {}

    public Comentariu(String[] comm) {
        this.comm = comm;
    }

    public static boolean checkId(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(commentsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[1].equals(id)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static boolean checkLiked(String id, String user) {
        try (BufferedReader br = new BufferedReader(new FileReader(likedCommPath))) {
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

    public static boolean deletable(String user, String id) {   //checks if the comment exists
        try (BufferedReader br = new BufferedReader(new FileReader(commentsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user)) {
                    if (data[1].equals(id))
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
        try (BufferedReader br = new BufferedReader(new FileReader(likedCommPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(id)) {    //check if the comment is in the liked file
                    likes++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return likes;
    }

    public static void commentPost(String[] strings) {
        String[] user;
        String[] pass;
        String[] post;
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
            post = strings[3].split(" ");
            post[1] = post[1].replace("'", "");
        } else {
            System.out.println("{ 'status' : 'error', 'message' : 'No text provided'}");
            return;
        }

        if (Postare.checkId(post[1])) {    //cannot comment a post that doesn't exist
            System.out.println("{ 'status' : 'error', 'message' : 'Post doesn't exist'}");
            return;
        }

        if (strings.length >= 5) {
            text = strings[4].split(" ");
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
            System.out.println("{ 'status' : 'error', 'message' : 'Comment text length exceeded'}");
            return;
        }

        commId++;

        try (FileWriter fw = new FileWriter(commentsPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(user[1] + ',' + commId + ',' + DateTimeFormatter.ofPattern("dd-MM-yyy").format(date) + ',' + post[1] + ',');

            for (String s : text) {
                out.print(s + " ");
            }

            out.print("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Comment added successfully'}");
    }

    public static void deleteComm(String[] strings) {
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

        if (checkId(id[1])) {   //does the comment exist?
            System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
            return;
        }

        if (deletable(user[1], id[1])) {   //the owner is trying to delete it
            System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(commentsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1]) && data[1].equals(id[1])) {
                    FileWriter fw = new FileWriter(commentsPath, true);
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

    public static void likeComm(String[] strings) {
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
            System.out.println("{ 'status' : 'error', 'message' : 'No comment identifier to like was provided'}");
            return;
        }

        if (checkId(id[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to like was not valid'}");
            return;
        }

        if (!checkLiked(id[1], user[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to like was not valid'}");
            return;
        }

        try (FileWriter fw = new FileWriter(likedCommPath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(id[1] + ',' + user[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }

    public static void unlikeComm(String[] strings) {
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
            System.out.println("{ 'status' : 'error', 'message' : 'No comment identifier to unlike was provided'}");
            return;
        }

        if (checkId(id[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to unlike was not valid'}");
            return;
        }

        if (checkLiked(id[1], user[1])) {
            System.out.println("{ 'status' : 'error', 'message' : 'The comment identifier to unlike was not valid'}");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(likedCommPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if(data[0].equals(user[1]) && data[1].equals(id[1])) {
                    FileWriter fw = new FileWriter(likedCommPath, true);
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

    public static ArrayList<String> getCommUser(String id) {
        ArrayList<String> comments = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(commentsPath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[1].equals(id)) {
                    comments.add(line); //collect all the comments
                }
            }

            comments.sort(Collections.reverseOrder());

            try (FileWriter fw = new FileWriter(detailsPath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                for (String s : comments) {
                    out.println(s); //no idea why I write the comments in the file :)
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }
}
