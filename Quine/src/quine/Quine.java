package quine;
public class Quine
{
  public static void main( String[] args )
  {
    char q = 34;
    String[] l = {
        "package quine;",
    "public class Quine",
    "{",
    "  public static void main( String[] args )",
    "  {",
    "    char q = 34;",
    "    String[] l = {",
    "    ",
    "    };",
    "    for( int i = 0; i < 7; i++ )",
    "        System.out.println( l[i] );",
    "    for( int i = 0; i < l.length; i++ )",
    "        System.out.println( l[7] + q + l[i] + q + ',' );",
    "    for( int i = 8; i < l.length; i++ )",
    "        System.out.println( l[i] );",
    "  }",
    "}",
    };
    for( int i = 0; i < 7; i++ )
        System.out.println( l[i] );
    for( int i = 0; i < l.length; i++ )
        System.out.println( l[7] + q + l[i] + q + ',' );
    for( int i = 8; i < l.length; i++ )
        System.out.println( l[i] );
  }
}