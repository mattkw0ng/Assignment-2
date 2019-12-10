import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PathFinder{

    private static Hashtable<String,Actor> collection = new Hashtable<>();
    private static Hashtable<String,String> undercase_check = new Hashtable<>();

    /* -------------------------------- FIND PATH -------------------------------- */

    private static ArrayList<String> getShortestPath(String a1, String a2){  //finding actor2 from actor1
        if(a1.equals(a2)){      //if these are the same actor, returns null
            System.out.println("These are the same actor");
            return null;
        }

        boolean found = false;
        ArrayList<String> path = new ArrayList<String>();   //stores the path between the two actors
        Hashtable<String,String> visited = new Hashtable<>();   //keeps track of the visited actors and holds the parent actor for each new actor
        ArrayQueue<String> queue = new ArrayQueue<>();

        queue.enqueue(a1);
        visited.put(a1,a1);
        String next = "";
        while(!queue.empty()){
            next = queue.dequeue();
            if(next.equals(a2)){
                System.out.print("Found Path: ");
                found = true;
                break;
            }
            Actor a_next = collection.get(next);
            List<String> collect_network= a_next.network;
            for(String costar : collect_network){
                if(!visited.containsKey(costar)){
                    queue.enqueue(costar);
                    visited.put(costar,a_next.name); //stores the name of the actor, and the "parent" actor
                }
            }

        }

        if(!found){     //if no path is found, returns null
            System.out.println("No path found");
            return null;
        }

        String current_actor = next;
        while(!current_actor.equals(a1)){
            path.add(0,current_actor);  //adds to the end of the list
            current_actor = visited.get(current_actor);    //returns the "parent" Actor name
        }
        path.add(0,a1);
        return path;

    }

    /* -------------------------------- FILL TABLE -------------------------------- */

    private static void fillTable(String file) {
        try {
            /*  Converting file to Arraylist of Strings */
            ArrayList<String> fields = CSVParse(file);
            if(fields == null){
                return;
            }
            /*  Converts Strings to actor names */
            JSONParser parser = new JSONParser();
            for (String line : fields) {
                Object casting = parser.parse(line);
                JSONArray array = (JSONArray) casting;  //converts string to JSONArray


                /*  Converting JSONArray to Array of Strings    */
                ArrayList<String> actorNames = new ArrayList<>();   //stores the names
                JSONObject actor;
                for(Object temp : array){
                    actor = (JSONObject) temp;
                    Object actor_name = actor.get("name");  //gets the name
                    String converted = (String) actor_name;
                    actorNames.add(converted);    //adds the name
                }

                /*     Creating a temporary network by adding all of the actors to it    */
                Hashtable<String,String> temp_network = new Hashtable<>();
                for (String name : actorNames) {
                    temp_network.put(name, name);
                }

                /*      Adding actors to collection     */
                for (String current_actor : actorNames) {

                    /*  If this is a new actor, creates a new actor and adds to the collection */
                    if(!collection.containsKey(current_actor)){
                        Actor temp_actor = new Actor(current_actor,actorNames);
                        collection.put(temp_actor.name, temp_actor);
                        undercase_check.put(temp_actor.name.toLowerCase(),temp_actor.name);
                    }
                    /*  If this actor is already in the database, updates their network */
                    else{
                        Actor found_actor = collection.remove(current_actor);
                        found_actor.updateNetwork(actorNames);
                        collection.put(current_actor,found_actor);
                    }
                }
            }
        } catch(ParseException e){
            e.printStackTrace();
            collection = null;
        }catch (NullPointerException e){
            e.printStackTrace();
            collection = null;
        }catch (Exception e) {
            e.printStackTrace();
            collection = null;
        }
    }

    /* -------------------------------- CSV PARSE -------------------------------- */

    private static ArrayList<String> CSVParse(String filepath){
        try {
            System.out.println("parsing through '" + filepath + "'...");
            File file = new File(filepath);
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            ArrayList<String> cast_members = new ArrayList<>();
            int c;
            int index = -1;  //we only need the cast member list

            while ((c = buffer.read()) != -1) {
                char character = (char) c;
                String field = "";
                System.out.print("parsing "+(index/2)+"/4802\r");

                if (character=='[') {

                    index ++;   //we have found a new field
                    field+=character;
                    char previous = character;  //keeps track of the previous character

                    while ((c = buffer.read()) != -1) {
                        character = (char) c;

                        /*  Special Cases  */
                        if(character==']' && previous =='}'){  //all fields will end with "...}]"
                            field+=']'; //ends the field
                            break;
                        }else if(character==']' && previous =='['){ //for the movies with no cast members
                            field = "empty";    //marks the field as empty
                            break;
                        }else if(character == ']' && field.contains("REC")){  //not a real field... "[REC]" is a movie title
                            index --;
                            break;
                        } else if(character=='\"' && previous=='\"'){     //removing unnecessary ""
                            previous = 'n'; // makes sure it does not remove too many "" in the case of """ or """"
                        }

                        /*  Growing the String  */
                        else{
                            field += character;
                            previous = character;
                        }

                    }

                    /*  only adds the cast members and does not add empty fields    */
                    if(index%2 == 0 && !field.equals("empty")){ //cast member indexes occur as even numbers
                        cast_members.add(field);
                    }
                }
            }
            buffer.close();
            System.out.println("Done parsing   --   lines: "+(index/2));
            return cast_members;
        }catch (FileNotFoundException e){
            System.out.println("-- Error: file not found -- ");
            return null;
        }catch (Exception e){
            System.out.println(("Other error"));
            return null;
        }
    }

    /* -------------------------------- PRINT PATH -------------------------------- */

    private static void printPath(ArrayList<String> path){
        if(path!=null){     //makes sure the path is not null
            for(int i = 0 ; i<path.size()-1 ; i++){
                System.out.print(path.get(i)+" --> ");
            }
            System.out.print(path.get(path.size()-1) + "\n");
        }
    }

    /* -------------------------------- GET INPUT -------------------------------- */

    private static String[] getInput(){
        Scanner scan = new Scanner(System.in);
        String[] actors = new String[2];
        System.out.println("Actor 1 name: ");
        String actor1 = scan.nextLine().toLowerCase();

        while(!undercase_check.containsKey(actor1)){    //instantly checks if the input is a valid name
            System.out.println("Actor not found.\nActor 1 name: ");
            actor1 = scan.nextLine().toLowerCase();
        }
        actors[0] = undercase_check.get(actor1);

        System.out.println("Actor 2 name: ");
        String actor2 = scan.nextLine().toLowerCase();
        while(!undercase_check.containsKey(actor2)){
            System.out.println("Actor not found.\nActor 2 name: ");
            actor2 = scan.nextLine().toLowerCase();
        }
        actors[1] = undercase_check.get(actor2);
        return actors;
    }

    public static void main(String[] args){
        if(args.length==1){
            String file = args[0];
            fillTable(file);  //processes the file and fills the collection hashtable
            Scanner scan = new Scanner(System.in);
            if(collection!=null){

                //printNetwork(collection,0);
                boolean cntnue = true;
                while(cntnue) {
                    /*  Gets two valid actors and finds the path between them  */
                    String[] actors = getInput();
                    ArrayList<String> path = getShortestPath(actors[0], actors[1]);
                    printPath(path);
                    System.out.println("Continue? (type 'yes' to continue) ");
                    String yes = scan.nextLine();
                    if(!yes.equals("yes")&&!yes.equals("Yes")){
                        cntnue = false;
                    }
                }
            }

        }else{
            System.out.println("Incorrect usage of program");
            System.out.println("--java PathFinder 'filepath'");
        }
    }
}