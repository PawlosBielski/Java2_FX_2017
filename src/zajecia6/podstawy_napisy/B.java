package zajecia6.podstawy_napisy;

import java.util.Scanner;

public class B {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);




        int poz = 0;
        int leftBorder = -5, righBorder = 5;
        char c = '1';
        String wszystkie = "l,r,x";
        String wprawo = "r,x";
        String wlewo = "l,x";
        while(c!='x') {
            System.out.print("Twój ruch? (");
            if (poz==5) {
                System.out.print(wlewo);
            } else if (poz==-5) {
                System.out.print(wprawo);
            } else {
                System.out.print(wszystkie);
            }
            System.out.print(")");

            c = s.next().charAt(0);
            if (c=='l' && poz>-5) poz--;
            if (c=='r' && poz<5) poz++;
//            System.out.println("Pozycja=" + poz);
            String pole = "";
            for (int i = -5; i <= 5; i++) {
                if (i!=poz) pole += ".";
                else pole += "@";
            }
            System.out.println(pole);
        }

    }
}
