import java.util.Scanner;
import java.util.Queue;
import java.util.Hashtable;

public class PuzzleBFS{
    public static int[] solution_table = new int[16];
    public static String solution_str = "";

    public static boolean isGoal(String table){
        return solution_table.equals(table);
    }
    public static boolean isSolvable(int[] table, int row){
        int inversions = 0,tmp = 0;
        boolean bottom_odd = false;

        if( row == 0 || row == 2) bottom_odd = true;
        for(int i=0; i<16; i++){
            if(table[i]==0) continue;
            for(int j = i+1; j<16; j++)
                if(table[j] < table[i] && table[j] != 0)
                    inversions++;
        }
        System.out.println(bottom_odd);
        System.out.println(inversions);
        return bottom_odd == (inversions %2 == 0);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[] initial = new int[16];
        String str = "";
        int tmp, blank_row=0;

        //Scan Initial State Table
            int k = 0;
            for(int i = 0; i<4; i++){
                for(int j = 0; j<4; j++){
                    tmp = in.nextInt();
                    initial[k] = tmp;
                    //System.out.println(tmp+" "+i+" "+j+" "+k+" "+initial[k]);
                    str += tmp + " ";
                    if(tmp == 0) blank_row = i;
                    k++;
                }
            }
            //System.out.println();
        //Scan Final State Table
            for(int i = 0; i<16; i++){
                tmp = in.nextInt();
                solution_table[i] = tmp;
                solution_str += tmp + " ";
                //System.out.println(tmp+" "+i);
            }
        //Tests
        if(isGoal(str)){
            //Open function to return:
                //the number of moves to reach solution
                //the sequence of moves
                //the time consumed
                //the number of nodes created
                //the number of nodes used/in Hashtable
        }
        else{
            if(isSolvable(initial,blank_row)){
                System.out.println("Is isSolvable");
            }
            else{
                System.out.println("Solution not Found");
                System.exit(0);
            }
        }
    }
}
