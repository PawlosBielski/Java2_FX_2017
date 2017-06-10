package zajecia6.podstawy_napisy;

public class Warunki {
    public static void main(String[] args) {
        int x = 6;

        //jesli jest podzielne przez 2 to napisz "dwa", jeśli nie, ale podzielne przez 3 to napisz 3
        if (x%2==0) {
            System.out.println("dwa");
        } else if (x%3==0) {
            System.out.println("trzy");
        }

        //jesli podzielne przez 2 to napisz "2", dodatkowo jesli podzielne przez 3 to napisz 3
        if (x%2==0) {
            System.out.println("2");
        }
        if (x%3==0) {
            System.out.println("3");
        }
    }
}
