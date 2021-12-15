using System;

using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class ToolLibrarySystem : iToolLibrarySystem
    {
        public MemberCollection memberCollection = new MemberCollection();
        public ToolCollection toolCollection = new ToolCollection();
        public Member loggedInMember;
        public bool staffLoggedIn = false;

        public void add(Tool aTool)
        {
            if (!toolCollection.search(aTool))
            {
                toolCollection.add(aTool);
            }
        }

        public void add(Tool aTool, int quantity)
        {
            if (toolCollection.search(aTool))
            {
                Tool temp = aTool;
                temp.Quantity += quantity;
                toolCollection.update(aTool, temp);
            }
        }

        public Tool getToolByName(string name)
        {
            foreach (Tool t in Program.library.toolCollection.toArray())
            {
                if (t.Name.ToLower().Equals(name.ToLower()))
                {
                    return t;
                }
            }
            return null;
        }

        public void borrowTool(Member aMember, Tool aTool)
        {
            if (aTool.AvailableQuantity > 0)
            {
                aMember.addTool(aTool);
            }
        }

        public void returnTool(Member aMember, Tool aTool)
        {
            if (!aMember.getBorrowedTools().Contains(aTool)) return;
            aMember.deleteTool(aTool);
        }

        public void delete(Tool aTool)
        {
            if (!toolCollection.search(aTool))
            {
                return; //return if tool doesnt exist
            }
            toolCollection.delete(aTool);
        }

        public void delete(Tool aTool, int quantity)
        {
            if (!toolCollection.search(aTool) || quantity > aTool.AvailableQuantity)
            {
                return; //return if tool doesnt exist or attempting to delete more than available
            }
            Tool temp = aTool;
            temp.Quantity -= quantity;
            toolCollection.update(aTool, temp);
        }
        public void add(Member aMember)
        {
            if (!memberCollection.search(aMember)) memberCollection.add(aMember);
        }

        public void delete(Member aMember)
        {
            if (!memberCollection.search(aMember))
            {
                return; //return if member doesnt exist
            }
            memberCollection.delete(aMember);
        }

        public Member getMemberByName(string name)
        {
            foreach (Member m in memberCollection.toArray())
            {
                if (m != null)
                {
                    if (m.getFullName().ToLower().Equals(name.ToLower()))
                    {
                        return m;
                    }
                }
            }
            return null;
        }

        public void displayBorrowingTools(Member aMember)
        {
            foreach (string s in listTools(aMember))
            {
                Console.WriteLine(s);
            }
        }

        public void displayTools(string aToolType)
        {
            Tool[] toolsByType = toolCollection.GetToolsByType(aToolType);
            if (toolsByType.Length == 0)
            {
                Console.WriteLine("There are no tools of this type yet");
                return;
            }
            for (int i = 0; i < toolsByType.Length; i++)
            {
                Tool t = toolCollection.GetToolsByType(aToolType)[i];
                Console.WriteLine("Tool: " + t.Name + "    Available: " + t.AvailableQuantity);
            }
        }

        public void displayTopTHree()
        {
            Tool[] allTools = toolCollection.toArray();
            ToolCollection.InsertionSort(allTools);

            for (int i = 0; i < allTools.Length; i++)
            {
                if (i < 3)
                {
                    Tool h = allTools[i];
                    int rank = i + 1;
                    Console.WriteLine(rank + ". " + h.ToString() + ", Borrowings: " + h.NoBorrowings);
                }
            }
        }

        public string[] listTools(Member aMember)
        {
            return aMember.Tools;
        }


    }
}
