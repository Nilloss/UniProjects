using System;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class MemberMenuOptions
    {
        public static bool DisplayToolsByType()
        {
            string tooltype = "";

            writeLine("Select tool category");
            writeLine("1 - Gardening");
            writeLine("2 - Flooring");
            writeLine("3 - Fencing");
            writeLine("4 - Measuring");
            writeLine("5 - Cleaning");
            writeLine("6 - Painting");
            writeLine("7 - Electronic");
            writeLine("8 - Electricity");
            writeLine("9 - Automotive");

            CATEGORY category = (CATEGORY)acceptDigitInput(1, 9) - 1;

            Console.Clear();
            writeLine("Select tool Type");

            if (category == CATEGORY.Gardening)
            {
                string[] types = TYPE.GardeningTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Flooring)
            {
                string[] types = TYPE.FlooringTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Fencing)
            {
                string[] types = TYPE.FencingTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Measuring)
            {
                string[] types = TYPE.MeasuringTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Cleaning)
            {
                string[] types = TYPE.CleaningTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Painting)
            {
                string[] types = TYPE.PaintingTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Electronic)
            {
                string[] types = TYPE.ElectronicTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Electricity)
            {
                string[] types = TYPE.ElectricityTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (category == CATEGORY.Automotive)
            {
                string[] types = TYPE.AutomotiveTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tooltype = types[(acceptDigitInput(1, types.Length)) - 1];
            }

            Console.WriteLine(tooltype);

            Console.Clear();

            Program.library.displayTools(tooltype);

            if (Program.library.toolCollection.GetToolsByType(tooltype).Length == 0) return false;

            return true;
        }
        public static bool BorrowATool()
        {
            writeLine("Please selection an option, or press 0 to return to menu");
            writeLine("1 - Browse tool library");
            writeLine("2 - Search tool library");

            ConsoleKeyInfo readKey = Console.ReadKey();

            if (readKey.Key.Equals(ConsoleKey.D1))
            {
                Console.Clear();
                if (!DisplayToolsByType()) return false;
                Console.Write("Enter name of tool to borrow: ");
                string name = Console.ReadLine();
                Tool toBorrow = Program.library.getToolByName(name);
                if (toBorrow == null) { writeLine("Tool not found"); return false; }
                Program.library.borrowTool(Program.library.loggedInMember, toBorrow);
                writeLine("You are now borrowing a tool: " + toBorrow.Name);
            } 
            else if (readKey.Key.Equals(ConsoleKey.D2)){
                Console.Clear();
                Console.Write("Enter tool name: ");
                Tool toBorrow = Program.library.getToolByName(Console.ReadLine());
                if(toBorrow != null)
                {
                    writeLine("Tool found - " + toBorrow.Name + "   Available Qty: " + toBorrow.AvailableQuantity);
                    writeLine("Borrow? (Y/N)");

                    char input = 'h';

                    while (input != 'y' && input != 'n'){
                        input = Console.ReadKey(false).KeyChar;
                    }

                    if (input == 'y')
                    {
                        Program.library.borrowTool(Program.library.loggedInMember, toBorrow);
                        writeLine("You are now borrowing a tool: " + toBorrow.Name);
                        return true;
                    }
                }
                else
                {
                    writeLine("Tool not found");
                    return false;
                }
            }
            return true;
        }
        public static bool ReturnATool()
        {
            if (ListAllMyTools())
            {
                Console.Write("Enter name of tool to return: ");
                Tool toReturn = Program.library.getToolByName(Console.ReadLine());
                Program.library.returnTool(Program.library.loggedInMember, toReturn); ;
                writeLine("You have returned a tool: " + toReturn.Name);
            }
            return true;
        }
        public static bool ListAllMyTools()
        {
            writeLine("You are currently borrowing the following tools: ");
            string[] myTools = Program.library.listTools(Program.library.loggedInMember);
            foreach (string s in myTools)
            {
                writeLine(s);
            }
            return true;
        }
        public static bool DisplayTopThree()
        {
            Program.library.displayTopTHree();
            return true;
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

        private static int acceptNumberInput(string consoleMessage)
        {
            int n = -1;
            while (n == -1)
            {
                Console.Write(consoleMessage);
                string inp = Console.ReadLine();
                if (System.Text.RegularExpressions.Regex.IsMatch(inp, @"^\d+$")) n = int.Parse(inp);
            }
            return n;
        }

        private static int acceptNumberInput()
        {
            int n = -1;
            while (n == -1)
            {
                string inp = Console.ReadLine();
                if (System.Text.RegularExpressions.Regex.IsMatch(inp, @"^\d+$")) n = int.Parse(inp);
            }
            return n;
        }
    }
}
