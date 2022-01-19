using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text.RegularExpressions;

namespace ToolLibrary
{
    class Program
    {
        public static ToolLibrarySystem library = new ToolLibrarySystem();
        private static int currMenu = 10;
        public  static bool testing = false;

        private static Stopwatch stopwatch = new Stopwatch();
        public static double averageSortTime(int n)
        {
            double sum = 0;

            for(int i = 0; i < 100; i++)
            {
                
                Tool[] randomTools = generateRandomTools(n);
                stopwatch.Start();
                ToolCollection.InsertionSort(randomTools);
                stopwatch.Stop();
                double elapsedTime = (stopwatch.ElapsedMilliseconds);
                double microSeconds = elapsedTime * 1000;
                sum += microSeconds;
            }

            return (sum / 100);
        }

        public static Tool[] generateRandomTools(int n)
        {
            List<Tool> tools = new List<Tool>();
            for(int i = 0; i < n; i++)
            {
                Tool t = new Tool();
                int seed = (int)DateTime.Now.Ticks;
                Random rnd = new Random(seed);
                int val = rnd.Next(0, n);
                t.NoBorrowings = val;
                tools.Add(t);
            }
            return tools.ToArray();
        }


        static void Main(string[] args)
        {
            if (testing)
            {
                macros.print("Average sort time at n = 10" + averageSortTime(10) + " microseconds");

                macros.print("Average sort time at n = 100" + averageSortTime(100) + " microseconds");

                macros.print("Average sort time at n = 1000" + averageSortTime(1000) + " microseconds");
            }
            else
            {
                bool useMenu = true;
                while (useMenu)
                {
                    Console.Clear();
                    switch (currMenu)
                    {
                        case 0:
                            useMenu = false;
                            break;
                        case 1:
                            staffMenu();
                            break;
                        case 2:
                            memberMenu();
                            break;
                        case 10:
                            mainMenu();
                            break;
                    }
                }
            }
        }

        private static void mainMenu()
        {
            writeLine("Welcome to the Tool Library");
            writeLine("============Main Menu===========");
            writeLine("1. Staff Login");
            writeLine("2. Member Login");
            writeLine("0. Exit");
            writeLine("");
            Console.Write("Please make a selection (1-2, or 0 to exit):");

            int selection = acceptDigitInput(0, 2);

            currMenu = selection;
        }



        private static void staffMenu()
        {
            if (!library.staffLoggedIn)
            {
                writeLine("Please enter staff login details");
                Console.Write("Username: ");
                string username = Console.ReadLine();
                Console.Write("Password: ");
                string password = Console.ReadLine(); //implement hidden password solution here https://social.msdn.microsoft.com/Forums/vstudio/en-US/6f8a90d9-be27-49de-84cb-b960985a0ef9/console-how-to-hide-received-input?forum=csharpgeneral

                if (!username.Equals("staff") || !password.Equals("today123"))
                {
                    writeLine("Incorrect login details, press any key to return to main menu or 1 to try again");
                    ConsoleKeyInfo readKey = Console.ReadKey();
                    if (!readKey.Key.Equals(ConsoleKey.D1)) currMenu = 10;
                    return;
                }

                library.staffLoggedIn = true;
            }


            Console.Clear();
            writeLine("Welcome to the Tool Library");
            writeLine("===============Staff Menu===============");
            writeLine("1. Add a new tool");
            writeLine("2. Add new pieces of an existing tool");
            writeLine("3. Remove some pieces of a tool");
            writeLine("4. Register a new member");
            writeLine("5. Remove a member");
            writeLine("6. Find the contact number of a member");
            writeLine("0. Return to main menu");
            writeLine("");
            Console.Write("Please make a selection (1-2, or 0 to exit):");

            int selection = acceptDigitInput(0, 6);
            staffMenuSwitch(selection);
        }

        private static void staffMenuSwitch(int selection)
        {

            switch (selection)
            {
                case 0: //Return to main menu
                    library.staffLoggedIn = false;
                    currMenu = 10;
                    break;
                case 1: //Add a new tool
                    Console.Clear();
                    if (StaffMenuOptions.AddNewTool())
                    {
                        writeLine("Tool added, press any key to return to menu");
                    }
                    currMenu = 1;
                    Console.ReadKey();

                    break;
                case 2://Add new pieces to existic tool
                    Console.Clear();
                    if (StaffMenuOptions.AddPiecesToTool())
                    {
                        writeLine("Press any key to return to menu");
                        Console.ReadKey();
                    }
                    else
                    {
                        Console.Write("Press any key to return to menu or press 1 to try again");

                        ConsoleKeyInfo readKey = Console.ReadKey();

                        if (ConsoleKey.D1.Equals(readKey.Key))
                        {
                            staffMenuSwitch(2);
                        }
                    }
                    break;
                case 3://Remove some pieces of a tool
                    Console.Clear();
                    if (StaffMenuOptions.RemovePiecesFromTool())
                    {
                        writeLine("Press any key to return to menu");
                        Console.ReadKey();
                    }
                    else
                    {
                        Console.Write("Press any key to return to menu or press 1 to try again");

                        ConsoleKeyInfo readKey = Console.ReadKey();

                        if (ConsoleKey.D1.Equals(readKey.Key))
                        {
                            staffMenuSwitch(3);
                        }
                    }
                    break;
                case 4: // Register a member
                    Console.Clear();
                    if (StaffMenuOptions.RegisterMember())
                    {
                        writeLine("Press any key to return to menu");
                    }
                    Console.ReadKey();
                    break;
                case 5: //Remove a member
                    Console.Clear();
                    if (StaffMenuOptions.DeleteMember())
                    {
                        Console.Write("Press any key to return to menu");
                        Console.ReadKey();
                    }
                    else
                    {
                        Console.Write("Press any key to return to menu or press 1 to try again");

                        ConsoleKeyInfo readKey = Console.ReadKey();

                        if (ConsoleKey.D1.Equals(readKey.Key))
                        {
                            staffMenuSwitch(5);
                        }
                    }
                    break;
                case 6: //Find contact number of a member
                    Console.Clear();
                    if (StaffMenuOptions.FindContactNumber())
                    {
                        Console.Write("Press any key to return to menu");
                        Console.ReadKey();
                    }
                    else
                    {
                        Console.Write("Press any key to return to menu or press 1 to try again");

                        ConsoleKeyInfo readKey = Console.ReadKey();

                        if (ConsoleKey.D1.Equals(readKey.Key))
                        {
                            staffMenuSwitch(6);
                        }
                    }
                    break;
            }
        }
        private static void memberMenu()
        {

            if (library.loggedInMember == null)
            {

                Console.Write("Enter First Name: ");
                string firstName = Console.ReadLine();
                Console.Write("Enter Last Name: ");
                string lastName = Console.ReadLine();
                string fullName = firstName + " " + lastName;
                Member retrieved = library.getMemberByName(fullName);

                if (retrieved == null)
                {
                    writeLine("Member doesnt exist, press any key to return to main menu or 1 to try again");
                    ConsoleKeyInfo readKey = Console.ReadKey();
                    if (!readKey.Key.Equals(ConsoleKey.D1)) currMenu = 10;
                    return;
                }

                Console.Write("Enter pin: ");
                String pin = Console.ReadLine();
                if (pin.Equals(retrieved.PIN))
                {
                    library.loggedInMember = retrieved;
                }
                else
                {
                    writeLine("Invalid pin, press any key to return to main menu or 1 to try again");
                    ConsoleKeyInfo readKey = Console.ReadKey();
                    if (!readKey.Key.Equals(ConsoleKey.D1)) currMenu = 10;
                    return;
                }
            }

            Console.Clear();
            writeLine("Welcome to the Tool Library");
            writeLine("===============Member Menu==============");
            writeLine("1. Display all the tools of a tool type");
            writeLine("2. Borrow a tool");
            writeLine("3. Return a tool");
            writeLine("4. List all the tools that I am renting");
            writeLine("5. Display top three (3) most frequently rented tools");
            writeLine("0. Return to main menu");
            writeLine("========================================");
            writeLine("");
            Console.Write("Please make a selection (1-5, or 0 to return to main menu):");

            int selection = acceptDigitInput(0, 5);

            switch (selection)
            {
                case 0:
                    currMenu = 10;
                    library.loggedInMember = null;
                    break;
                case 1:
                    Console.Clear();
                    MemberMenuOptions.DisplayToolsByType();
                    writeLine("Press any key to return to menu");
                    Console.ReadKey();
                    break;
                case 2:
                    Console.Clear();
                    MemberMenuOptions.BorrowATool();
                    writeLine("Press any key to return to menu");
                    Console.ReadKey();
                    break;
                case 3:
                    Console.Clear();
                    MemberMenuOptions.ReturnATool();
                    writeLine("Press any key to return to menu");
                    Console.ReadKey();
                    break;
                case 4:
                    Console.Clear();
                    MemberMenuOptions.ListAllMyTools();
                    writeLine("Press any key to return to menu");
                    Console.ReadKey();
                    break;
                case 5:
                    Console.Clear();
                    MemberMenuOptions.DisplayTopThree();
                    writeLine("Press any key to return to menu");
                    Console.ReadKey();
                    break;
            }
        }

        private static int acceptDigitInput(int min, int max)
        {
            int digit = -1;

            while (digit == -1)
            {
                ConsoleKeyInfo readKey = Console.ReadKey();

                if (char.IsDigit(readKey.KeyChar))
                {
                    int readDigit = int.Parse(readKey.KeyChar.ToString());
                    if (readDigit >= min && readDigit <= max) digit = readDigit;
                }
            }
            return digit;
        }

        private static void writeLine(string s)
        {
            Console.WriteLine(s);

        }
    }
}
