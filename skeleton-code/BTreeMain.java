import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main Application.
 * <p>
 * You do not need to change this class.
 */
public class BTreeMain {

  public static void main(String[] args) {

    /** Create random number generator **/
    Random randGen = new Random();

    /** Read the input file -- input.txt */
    Scanner scan = null;
    try {
      scan = new Scanner(new File("input.txt")); // FIXME removed 'src/'
    } catch (FileNotFoundException e) {
      System.out.println("File not found.");
    }

    /** Read the minimum degree of B+Tree first */

    int degree = scan.nextInt();

    BTree bTree = new BTree(degree);

    /** Reading the database student.csv into B+Tree Node */
    List<Student> studentsDB = getStudents();

    for (Student s : studentsDB) {
      bTree.insert(s);
      // FIXME remove before submitting
      System.out.println(bTree.print());
    }



    /** Start reading the operations now from input file */
    try {
      while (scan.hasNextLine()) {
        Scanner s2 = new Scanner(scan.nextLine());

        while (s2.hasNext()) {

          String operation = s2.next();

          switch (operation) {
            case "insert": {

              long studentId = Long.parseLong(s2.next());
              String studentName = s2.next() + " " + s2.next();
              String major = s2.next();
              String level = s2.next();
              int age = Integer.parseInt(s2.next());

              long recordID = randGen.nextLong();

              Student s = new Student(studentId, age, studentName, major, level, recordID);
              bTree.insert(s);

              break;
            }
            case "delete": {
              long studentId = Long.parseLong(s2.next());
              boolean result = bTree.delete(studentId);
              if (result)
                System.out.println("Student deleted successfully.");
              else
                System.out.println("Student deletion failed.");

              break;
            }
            case "search": {
              long studentId = Long.parseLong(s2.next());
              long recordID = bTree.search(studentId);
              if (recordID != -1)
                System.out.println("Student exists in the database at " + recordID);
              else
                System.out.println("Student does not exist.");
              break;
            }
            case "print": {
              List<Long> listOfRecordID = new ArrayList<>();
              listOfRecordID = bTree.print();
              System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
            }
            default:
              System.out.println("Wrong Operation");
              break;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static List<Student> getStudents() {

    List<Student> studentList = new ArrayList<>();

    File studentFile = new File("Student.csv");

    if (studentFile.canRead()) {
      try {
        BufferedReader studentReader = new BufferedReader(new FileReader(studentFile));
        String row;
        while ((row = studentReader.readLine()) != null) { // read each row of Student.csv
          String[] data = row.split(","); // split the data in the row by comma

          // save each value (ID:long, age:int, name:String, major:String, level:String, recordID:
          // long)
          long id = Long.parseLong(data[0]);
          String name = data[1];
          String major = data[2];
          String level = data[3];
          int age = Integer.parseInt(data[4]);
          long recordId = Long.parseLong(data[5]);

          // create a new student and add it to studentList
          Student stu = new Student(id, age, name, major, level, recordId);
          studentList.add(stu);
        }

        studentReader.close();

        // after reading all of the lines in the Students.csv file in a buffer, delete the
        // contents of the file so that it can be easily re-populated.
        if (studentFile.exists() && studentFile.isFile()) {
          // FIXME studentFile.delete();
        }

        // FIXME studentFile.createNewFile();

      } catch (IOException e) {
        System.out.println("Cannot find Student.csv or cannot read a row.");
        return null;
      }
    } else {
      System.out.println("Cannot read Student.csv");
      return null;
    }

    return studentList;
  }
}
