using System;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class ToolCollection : iToolCollection
    {
        //An object of this class can be used to store a collection of tools that
        //are being rented by a member or a collection of tools of a tool type.

        private Tool[] tools = new Tool[0];

        public int Number => tools.Length;

        public void add(Tool aTool)
        {
            Tool[] temp = tools;//Make a copy of current tool array

            tools = new Tool[tools.Length+1];//make tool array bigger to accommodate new tool

            for (int i = 0; i < temp.Length; i++) tools[i] = temp[i];//Copy all tools over from temporary array to new tool array

            tools[tools.Length - 1] = aTool; //Assign tool to new spot in the array
        }

        public void update(Tool aTool, Tool updatedTool)
        {
            int index = getToolIndex(aTool);
            tools[index] = updatedTool;
        }

        public void delete(Tool aTool)
        {

            //Shift everything downward in current array
            int index = getToolIndex(aTool);
            for (int i = index + 1; i < tools.Length; i++)
            {
                tools[i - 1] = tools[i];
            }

            //Add to smaller array
            Tool[] temp = new Tool[tools.Length - 1];
            for (int i = 0; i < temp.Length; i++)
            {
                temp[i] = tools[i];
            }

            //Assign new array
            tools = temp;
        }

        public bool search(Tool aTool)
        {
            if(tools != null)
            {
                foreach (Tool t in tools)
                {
                    if (aTool.Equals(t))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public Tool[] toArray()
        {
            return tools;
        }

        public Tool[] GetRentedTools()
        {
            List<Tool> temp = new List<Tool>();
            foreach (Member m in Program.library.memberCollection.toArray())
            {
                foreach (Tool t in m.getBorrowedTools())
                {
                    if (!temp.Contains(t)) temp.Add(t);
                }
            }
            return temp.ToArray();
        }

        public Tool[] GetToolsByCategory(CATEGORY category)
        {
            List<Tool> temp = new List<Tool>();
            foreach (Tool t in tools)
            {
                if (t.Category == category) temp.Add(t);
            }
            return temp.ToArray();
        }

        public Tool[] GetToolsByType(string type)
        {
            List<Tool> temp = new List<Tool>();
            foreach (Tool t in tools)
            {
                if (t.Type.Equals(type)) temp.Add(t);
            }
            return temp.ToArray();
        }

        public static void InsertionSort(Tool[] A)
        {
            int n = A.Length;

            for (int i = 1; i <= (n - 1); i++)
            {
                Tool v = A[i];
                int j = i - 1;
                while (j >= 0 && A[j].NoBorrowings < v.NoBorrowings)
                {
                    A[j + 1] = A[j];
                    j = j - 1;
                }
                A[j + 1] = v;
            }
        }

        private int getToolIndex(Tool aTool)
        {
            for (int i = 0; i < tools.Length; i++)
            {
                Tool k = tools[i];
                if (aTool.Equals(k)) return i;
            }
            return -1;
        }
    }



    public enum CATEGORY
    {
        Gardening,
        Flooring,
        Fencing,
        Measuring,
        Cleaning,
        Painting,
        Electronic,
        Electricity,
        Automotive
    }

    //public class CATEGORY : List<TYPE>
    //{
    //    private static CATEGORY initialise = new CATEGORY();//This is for initialising the categories

    //    public static List<CATEGORY> categories = new List<CATEGORY>();

    //    public string Name;

    //    public CATEGORY()
    //    {
    //        CATEGORY Gardening = new CATEGORY();
    //        Gardening.Name = "Gardening";
    //        Gardening.Add(TYPE.Line_Trimmers);
    //    }

    //    Gardening,
    //    Flooring,
    //    Fencing,
    //    Measuring,
    //    Cleaning,
    //    Painting,
    //    Electronic,
    //    Electricity,
    //    Automotive
    //}

    public class TYPE
    {
        //Gardening
        public static string Line_Trimmers = "Line Trimmers";
        public static string Lawn_Mowers = "Lawn Mowers";
        public static string Hand_Tools = "Hand Tools";
        public static string Wheelbarrows = "Wheelbarrows";
        public static string Garden_Power = "Garden Power Tools";

        public static string[] GardeningTypes = { Line_Trimmers, Lawn_Mowers, Hand_Tools, Wheelbarrows, Garden_Power };

        //Flooring
        public static string Scrapers = "Scrapers";
        public static string Floor_Lasters = "Floor Lasers";
        public static string Floor_Levelling = "Floor Levelling Tools";
        public static string Floor_Levelling_Materials = "Floor Levelling Materials";
        public static string Floor_Hand = "Floor Hand Tools";
        public static string Tiling = "Tiling Tools";

        public static string[] FlooringTypes = { Scrapers, Floor_Lasters, Floor_Levelling, Floor_Levelling_Materials, Floor_Hand, Tiling };

        //Fencing
        public static string Hand = "Hand Tools";
        public static string Electric_Fencing = "Electric Fencing";
        public static string Steel_Fencing = "Steel Fencing Tools";
        public static string Power = "Power Tools";
        public static string Fencing_Accessory = "Fencing Accessories";

        public static string[] FencingTypes = { Hand, Electric_Fencing, Steel_Fencing, Power, Fencing_Accessory };

        //Measuring
        public static string Distance = "Distance Tools";
        public static string Laser_Measuring = "Laser Measurer";
        public static string Measuring_Jug = "Measuring Jugs";
        public static string Temperature_and_Humidity = "Temperature & Humidity Tools";
        public static string Levelling = "Levelling Tools";
        public static string Markers = "Markers";

        public static string[] MeasuringTypes = { Distance, Laser_Measuring, Measuring_Jug, Temperature_and_Humidity, Levelling, Markers };

        //Cleaning
        public static string Draining = "Draining";
        public static string Car_Cleaning = "Car Cleaning";
        public static string Vacuum = "Vacuum";
        public static string Pressure_Cleaning = "Pressure Cleaners";
        public static string Pool_Cleaning = "Pool Cleaning";
        public static string Floor_Cleaning = "Floor Cleaning";

        public static string[] CleaningTypes = { Draining, Car_Cleaning, Vacuum, Pressure_Cleaning, Pressure_Cleaning, Pool_Cleaning, Floor_Cleaning };


        //Painting
        public static string Sanding = "Sanding Tools";
        public static string Brushes = "Brushes";
        public static string Rollers = "Rollers";
        public static string Paint_Removal = "Paint Removal Tools";
        public static string Paint_Scrapers = "Paint Scrapers";
        public static string Sprayers = "Sprayers";

        public static string[] PaintingTypes = { Sanding, Brushes, Rollers, Paint_Removal, Paint_Scrapers, Sprayers };

        //Electronic 
        public static string Voltage_Testing = "Voltage Tester";
        public static string Oscilloscopes = "Oscilloscopes";
        public static string Thermal_Imaging = "Thermal Imaging";
        public static string Data_Test = "Data Test Tool";
        public static string Insulation_Testing = "Insulation Testers";

        public static string[] ElectronicTypes = { Voltage_Testing, Oscilloscopes, Thermal_Imaging, Data_Test, Insulation_Testing };

        //Electricity
        public static string Test_Equpiment = "Test Equipment";
        public static string Safety_Equipment = "Safety Equipment;";
        public static string Basic_Hand_Tools = "Basic Hand tools";
        public static string Circuit_Protection = "Circuit Protection";
        public static string Cable_Tools = "Cable Tools";

        public static string[] ElectricityTypes = { Test_Equpiment, Safety_Equipment, Basic_Hand_Tools, Circuit_Protection, Cable_Tools };

        //Automotive
        public static string Jacks = "Jacks";
        public static string Air_Compressors = "Air Compressors";
        public static string Battery_Chargers = "Battery Chargers";
        public static string Socket_Tools = "Socket Tools";
        public static string Braking = "Braking";
        public static string Drivetrain = "Drivetrain";

        public static string[] AutomotiveTypes = { Jacks, Air_Compressors, Battery_Chargers, Socket_Tools, Braking, Drivetrain };
    }
}
