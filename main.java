import java.io.*;
 

public class main
{
  static final char SPACE = ' ';
  static final char EOL = '#';
  static final String EMPTYSTRING = "";
  
  static class PartString
  {
    public int i = 0;
    public String string = "";
  }
  
  static void reportError(String s)
  {
    Console.writeLine(String.format("%-5s ", "*") + s + String.format("%6s", "*"));
  }
  
  static String stripLeadingSpaces(String transmission)
  {
    int transmissionLength = transmission.length();
    if (transmissionLength > 0)
    {
      char firstsignal = transmission.charAt(0);
      while (firstsignal == SPACE && transmissionLength > 0)
      {
        transmission = transmission.substring(1);
        transmissionLength -= 1;
        if (transmissionLength > 0)
        {
          firstsignal = transmission.charAt(0);
        }
      }
    }
    if (transmissionLength == 0)
    {
      reportError("No signal received");
    }
    return transmission;
  }
  
  static String stripTrailingSpaces(String transmission)
  {
    int lastChar = transmission.length() - 1;
    while (transmission.charAt(lastChar) == SPACE)
    {
      transmission = transmission.substring(0, lastChar);
      lastChar -= 1;
    }
    return transmission;
  }
  
 static String getTransmission()
  {
    boolean txt;
    String transmission;
    Console.write("\nEnter file name for receiving morse code: ");
    String fileName = Console.readLine();
    txt=(fileName.contains(".txt"));
    
    if(txt == false)
    {
        fileName = (fileName + ".txt");
    }
    if (txt == true)
    {
        fileName = fileName;
    }
    
    try
    {
      BufferedReader fileHandle = new BufferedReader(new FileReader(fileName));
      transmission = fileHandle.readLine();
      fileHandle.close();
      if (transmission == null) //Added to avoid null pointer exceptions later
      {
        transmission = EMPTYSTRING;
      }
      transmission = stripLeadingSpaces(transmission);
      if (transmission.length() > 0)
      {
        transmission = stripTrailingSpaces(transmission);
        transmission = transmission + EOL;
      }
    }
    catch (Exception e)
    {
      reportError("No file found try again");
      transmission = EMPTYSTRING;
      getTransmission();
    }
    return transmission;
  }
  
  static PartString getNextSymbol(int i, String transmission)
  {
    PartString partString = new PartString();
    String symbol = EMPTYSTRING;
    
    if (transmission.charAt(i) == EOL)
    {
      Console.writeLine();
      Console.writeLine("End of transmission");
      symbol = EMPTYSTRING;
    }
    else
    {
      int symbolLength = 0;
      char signal = transmission.charAt(i);
      while (signal != SPACE && signal != EOL)
      {
        i += 1;
        signal = transmission.charAt(i);
        symbolLength += 1;
      }
      if (symbolLength == 1)
      {
        symbol = ".";
      }
      else if (symbolLength == 3)
      {
        symbol = "-";
      }
      else if (symbolLength == 0)
      {
        symbol = SPACE + "";
      }
      else
      {
        reportError("Non-standard symbol received");
        symbol = EMPTYSTRING;
      }
    }
    partString.string = symbol;
    partString.i = i;
    return partString;
  }
  
  static PartString getNextLetter(int i, String transmission)
  {
    PartString partString = new PartString();
    PartString symbolString = new PartString();
    symbolString.string = EMPTYSTRING;
    boolean letterEnd = false;
    while (!letterEnd)
    {
      partString = getNextSymbol(i, transmission);
      String symbol = partString.string;
      i = partString.i;
      if (symbol == SPACE + "")
      {
        letterEnd = true;
        i += 4;
      }
      else if (transmission.charAt(i) == EOL)
      {
        letterEnd = true;
      }
      else if (transmission.charAt(i + 1) == SPACE && transmission.charAt(i + 2) == SPACE)
      {
        letterEnd = true;
        i += 3;
      }
      else
      {
        i += 1;
      }
      symbolString.string = symbolString.string + symbol;
      symbolString.i = i;
    }
    return symbolString;
  }
  
  static char decode(String codedLetter, int[] dash, char[] letter, int[] dot)
  {
    int codedLetterLength = codedLetter.length();
    int pointer = 0;
    char symbol;
    for (int i = 0; i < codedLetterLength; i++)
    {
      symbol = codedLetter.charAt(i);
      if (symbol == SPACE)
      {
        return SPACE;
      }
      else if (symbol == '-')
      {
        pointer = dash[pointer];
      }
      else
      {
        pointer = dot[pointer];
      }
    }
    return letter[pointer];
  }
  
  static void receiveMorseCode(int[] dash, char[] letter, int[] dot)
  {
    PartString partString = new PartString();
      
    int i = 0;
    String codedLetter;
    String plainText = EMPTYSTRING;
    String morseCodeString = EMPTYSTRING;
    boolean txt;
    
    String transmission = getTransmission();
    int lastChar = transmission.length() - 1;
    
    while (i < lastChar)
    {
      partString = getNextLetter(i, transmission);
      i = partString.i;
      codedLetter = partString.string;
      morseCodeString = morseCodeString + SPACE + codedLetter;
      char plainTextLetter = decode(codedLetter, dash, letter, dot);
      plainText = plainText + plainTextLetter;
    }
    
    Console.writeLine(morseCodeString);
    Console.writeLine(plainText);
   
    saveFile(plainText);
  }
  
  static void saveFile(String plainText){
    boolean txt;
    Console.println("how would you like to name a file for saving the translated morse code");
    String file = Console.readLine();
    
    txt = (file.contains(".txt"));
    if (txt == false)
    {
        file = (file + ".txt");
    }
    if (txt == true)
    {
        file = file;
    }

      try {
      File myObj = new File(file);
      
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
        FileWriter myWriter = new FileWriter(file);
          myWriter.write(plainText);
          myWriter.close();
      } 
      else {
        System.out.println("File already exists.Try again");
        sendReceiveMessages();
      }
    } 
    catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
  
  static void sendMorseCode(String[] morseCode)
  {

   String plainText = verification();
    
    
    int plainTextLength = plainText.length();
    String morseCodeString = EMPTYSTRING;
    int index;
    
    for (int i = 0; i < plainTextLength; i++)
    {
      

      char plainTextLetter = plainText.charAt(i);
      if (plainTextLetter == SPACE)
      {
        index = 0;
      }
      else
      {
        index = (int)plainTextLetter - (int)'A' + 1;
      }
      String codedLetter = morseCode[index];
      morseCodeString = morseCodeString + codedLetter + SPACE;
    }
    Console.writeLine(morseCodeString);
    sendSignal(morseCodeString);
  }

  static void sendSignal(String morseCodeString)
  {
    String output = EMPTYSTRING;
    for (int i = 0;i < morseCodeString.length(); i++){
      if (morseCodeString.charAt(i)== '.'){
        output = output+ "=" + SPACE;
      }
      else if (morseCodeString.charAt(i)== '-'){
        output = output + "===" + SPACE;
      }
      else if (morseCodeString.charAt(i)== SPACE){
        output = output + SPACE + SPACE;
      }
    }
    Console.writeLine(output);
  }

  static String verification() 
  {
    boolean loop = true;
    while (loop = true) {
        Console.write("Enter your message: ");
        String plainText = Console.readLine();

        if (plainText.toUpperCase() == plainText){
          loop = false;
          return plainText;
        }
        else{
          reportError("character is not an uppercase letter or a space");
        }
  }
    return verification();
    }


  
  static void displayMenu()
  {
    Console.writeLine();
    Console.writeLine("Main Menu");
    Console.writeLine("=========");
    Console.writeLine("R - Receive Morse code");
    Console.writeLine("S - Send Morse code");
    Console.writeLine("C - Translate morse code");
    Console.writeLine("X - Exit program");
    Console.writeLine();
  }

 
    static void sendReceiveMessages()
  {
    int[] dash = { 20, 23, 0, 0, 24, 1, 0, 17, 0, 21, 0, 25, 0, 15, 11, 0, 0, 0, 0, 22, 13, 0, 0, 10, 0, 0, 0 };
    int[] dot = { 5, 18, 0, 0, 2, 9, 0, 26, 0, 19, 0, 3, 0, 7, 4, 0, 0, 0, 12, 8, 14, 6, 0, 16, 0, 0, 0 };
    char[] letter = { ' ', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    String[] morseCode = { " ", ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.." };
    boolean programEnd = false;
    while (!programEnd)
    {
      displayMenu();
      Console.write("Enter your choice: ");
      String menuOption = Console.readLine();
      
    
      if (menuOption.equalsIgnoreCase("r"))
      {
        receiveMorseCode(dash, letter, dot);
      }
      else if (menuOption.equalsIgnoreCase("s"))
      {
        sendMorseCode(morseCode);
      }
      else if (menuOption.equalsIgnoreCase("x"))
      {
        programEnd = true;
      }
      else if(menuOption.equalsIgnoreCase("c"))
      {
          directly(dash, letter, dot);
      }
    
    }
  }
  
  static String directlyToMorse()
  {
      String transmission = Console.readLine();

      if (transmission == null) //Added to avoid null pointer exceptions later
      {
        transmission = EMPTYSTRING;
      }
      transmission = stripLeadingSpaces(transmission);
      if (transmission.length() > 0)
      {
        transmission = stripTrailingSpaces(transmission);
        transmission = transmission + EOL;
      }
      return transmission;
    }
 
    static void directly(int[] dash, char[] letter, int[] dot)
  {
    PartString partString = new PartString();
      
    int i = 0;
    String codedLetter;
    String plainText = EMPTYSTRING;
    String morseCodeString = EMPTYSTRING;
    boolean txt;
    
    String transmission = directlyToMorse();
    int lastChar = transmission.length() - 1;
    
    while (i < lastChar)
    {
      partString = getNextLetter(i, transmission);
      i = partString.i;
      codedLetter = partString.string;
      morseCodeString = morseCodeString + SPACE + codedLetter;
      char plainTextLetter = decode(codedLetter, dash, letter, dot);
      plainText = plainText + plainTextLetter;
    }
    
    Console.writeLine(morseCodeString);
    Console.writeLine(plainText);
   
    saveFile(plainText);
  }
  
    
    public static void main(String[] args) 
    {
      sendReceiveMessages();
    }
    
}
