public class test{
public boolean containsUpperCaseCharacter(String string) {
     for (int i = 0; i < string.length(); i++) {
       if (Character.isUpperCase(string.charAt(i))) {
         return true;
       }
     }
   
   
     return false;
   }
}