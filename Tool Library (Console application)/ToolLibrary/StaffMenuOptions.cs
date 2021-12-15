using System;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class StaffMenuOptions
    {
        public static bool AddNewTool()
        {

            Tool tool = new Tool();
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

            tool.Category = (CATEGORY)acceptDigitInput(1, 9) - 1;

            Console.Clear();
            writeLine("Select tool Type");

            if (tool.Category == CATEGORY.Gardening)
            {
                string[] types = TYPE.GardeningTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Flooring)
            {
                string[] types = TYPE.FlooringTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Fencing)
            {
                string[] types = TYPE.FencingTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Measuring)
            {
                string[] types = TYPE.MeasuringTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Cleaning)
            {
                string[] types = TYPE.CleaningTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Painting)
            {
                string[] types = TYPE.PaintingTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Electronic)
            {
                string[] types = TYPE.ElectronicTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Electricity)
            {
                string[] types = TYPE.ElectricityTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }
            else if (tool.Category == CATEGORY.Automotive)
            {
                string[] types = TYPE.AutomotiveTypes;
                for (int i = 0; i < types.Length; i++)
                {
                    writeLine((i + 1) + " - " + types[i]);
                }

                tool.Type = types[(acceptDigitInput(1, types.Length)) - 1];
            }

            Console.Clear();

            Console.Write("Enter tool name: ");
            tool.Name = Console.ReadLine();

            int n = acceptNumberInput("Enter tool quantity: ");
            tool.Quantity = n;
            Program.library.add(tool);

            return true;
        }

        public static bool AddPiecesToTool()
        {
            Console.Write("Enter tool name: ");
            string name = Console.ReadLine().ToLower();
            Tool retrieved = Program.library.getToolByName(name);
            if (retrieved != null)
            {
                writeLine("Tool found: " + retrieved.Name + ", with " + retrieved.Quantity + " total pieces, and " + retrieved.AvailableQuantity + " available pieces");
                writeLine("How many pieces would you like to add?");
                int pieces = acceptNumberInput();
                Program.library.add(retrieved, pieces);
                writeLine(retrieved.Name + " now has " + (retrieved.Quantity) + " total pieces, and " + (retrieved.AvailableQuantity) + " available pieces");

                return true;
            }
            else
            {

                Console.WriteLine("Tool not found");
                return false;
            }
        }

        public static bool RemovePiecesFromTool()
        {
            Console.Write("Enter tool name: ");
            string name = Console.ReadLine().ToLower();
            Tool retrieved = Program.library.getToolByName(name);
            if (retrieved != null)
            {
                writeLine("Tool found: " + retrieved.Name + ", with " + retrieved.Quantity + " total pieces, and " + retrieved.AvailableQuantity + " available pieces");
                writeLine("How many pieces would you like to remove? " + retrieved.AvailableQuantity + " pieces can be removed");
                int pieces = -1;
                while (pieces < 0 || pieces >= retrieved.AvailableQuantity)
                {
                    pieces = acceptNumberInput();
                }
                Program.library.delete(retrieved, pieces);
                writeLine(retrieved.Name + " now has " + (retrieved.Quantity) + " total pieces, and " + (retrieved.AvailableQuantity) + " available pieces");
                return true;
            }
            else
            {

                Console.WriteLine("Tool not found");
                return false;
            }
        }

        public static bool RegisterMember()
        {
            Console.Write("First Name: ");
            string firstname = Console.ReadLine();
            Console.Write("Last Name: ");
            string lastname = Console.ReadLine();
            Console.Write("Contact Number: ");
            string contactno = Console.ReadLine();
            Console.Write("Enter their PIN: ");
            string pin = Console.ReadLine();

            Member member = new Member();
            member.FirstName = firstname;
            member.LastName = lastname;
            member.ContactNumber = contactno;
            member.PIN = pin;

            Program.library.add(member);

            writeLine("Member added");

            return true;
        }

        public static bool DeleteMember()
        {
            Console.Write("Enter members First Name: ");
            string firstname = Console.ReadLine();
            Console.Write("Enter members Last Name: ");
            string lastname = Console.ReadLine();
            string fullname = firstname + " " + lastname;

            Member retrieved = Program.library.getMemberByName(fullname);
            if (retrieved != null)
            {
                Program.library.delete(retrieved);
                writeLine("Member deleted");
                return true;
            }
            else
            {
                writeLine("Member not found");
                return false;
            }
        }

        public static bool FindContactNumber()
        {
            Console.Write("Enter members First Name: ");
            string firstname = Console.ReadLine();
            Console.Write("Enter members Last Name: ");
            string lastname = Console.ReadLine();
            string fullname = firstname + " " + lastname;

            Member retrieved = Program.library.getMemberByName(fullname);
            if (retrieved != null)
            {
                writeLine("Members contact number is: " + retrieved.ContactNumber);
                return true;
            }
            else
            {
                writeLine("Member not found");
                return false;
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

        private static void writeLine(string s)
        {
            Console.WriteLine(s);

        }
    }
}
