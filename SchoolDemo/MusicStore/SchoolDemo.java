package MusicStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SchoolDemo 
{
    private static final String HELP_MESSAGE =
        "*** Commands: create, load, find <n>, instruments, accessories, students, quit\n" +
        "***           students [ <accessory> ], accessories [ <instrument> ]";
    
    public static void main(String args[]) 
    {
        BufferedReader stdin = 
                new BufferedReader(new InputStreamReader(System.in));
        String command;
                                    
        Class klasses[] = {Student.class, Instruments.class, 
                           Description.class, Accessories.class};
        HibernateContext.addClasses(klasses);

        do {
            System.out.print("\nCommand? ");
            
            try {
                command = stdin.readLine();
            }
            catch (java.io.IOException ex) {
                command = "?";
            }
            
            String parts[] = command.split(" ");
            
            if (command.equalsIgnoreCase("create")) {
                HibernateContext.createSchema();
            }
            else if (command.equalsIgnoreCase("load")) {
                Student.load();
                Instruments.load();
                Accessories.load();
            }
            else if (command.equalsIgnoreCase("instruments")) {
                System.out.println("this is one to one relation");
                Instruments.list();
            }
            else if (parts[0].equalsIgnoreCase("find") &&
                    (parts.length >= 2)) {
                long id = Long.parseLong(parts[1]);
                Student student = Student.find(id);
                
                if (student != null) {
                    student.printInSession();
                }
                else {
                    System.out.printf("*** No student with id %d\n", id);
                }
            }
            else if (parts[0].equalsIgnoreCase("students")) {
                switch (parts.length) {
                    case 1: System.out.println("this is many to many relation");Student.list(); break;
                    case 2: System.out.println("this is many to many relation");Student.studentsOf(parts[1]); break;
                }
            }
            else if (parts[0].equalsIgnoreCase("accessories")) {
                switch (parts.length) {
                    case 1: Accessories.list(); break;
                    case 2: System.out.println("this is many to one relation");Accessories.instrumentOf(parts[1]); break;
                }
            }
            else if (!command.equalsIgnoreCase("quit")) {
                System.out.println(HELP_MESSAGE);
            }
        } while (!command.equalsIgnoreCase("quit"));
    }
}
