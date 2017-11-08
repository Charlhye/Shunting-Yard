public  class Operator
{
    /**
     * Compares one operator over another
     *
     * @return if b=a, 0
     * @return if b>a, 1
     * @return if a>b, -1
     * */
    public static int precedence(char a, char b)
    {
        switch (a)
        {
            case '^':
                switch (b)
                {
                    case '^':
                        return 0;
                    case '*':
                    case '/':
                    case '+':
                    case '-':
                        return 1;
                }
            case '*':
            case '/':
                switch (b)
                {
                    case '^':
                        return -1;
                    case '*':
                    case '/':
                        return 0;
                    case '+':
                    case '-':
                        return 1;
                }
            case '+':
            case '-':
                switch (b)
                {
                    case '^':
                    case '*':
                    case '/':
                        return -1;
                    case '+':
                    case '-':
                        return 0;
                }
        }
        return -1000;
    }


}
