using System;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class Member : iMember
    {
        public Member LChild;
        public Member RChild;

        private string firstName;
        private string lastName;
        private string contactNumber;
        private string pin;
        private List<Tool> borrowedTools = new List<Tool>();

        public Member(string FirstName, string LastName, string ContactNo, string PIN)
        {
            this.firstName = FirstName;
            this.lastName = LastName;
            this.contactNumber = ContactNo;
            this.pin = PIN;
        }

        public Member()
        {

        }

        public string FirstName { get => firstName; set => firstName = value; }
        public string LastName { get => lastName; set => lastName = value; }
        public string ContactNumber { get => contactNumber; set => contactNumber = value; }
        public string PIN { get => pin; set => pin = value; }

        public string[] Tools => getBorrowedToolNames();

        public void addTool(Tool aTool)
        {
            borrowedTools.Add(aTool);
        }

        public void deleteTool(Tool aTool)
        {
            borrowedTools.Remove(aTool);
        }

        public List<Tool> getBorrowedTools()
        {
            return borrowedTools;
        }

        public string getFullName()
        {
            return firstName + " " + lastName;
        }

        public override string ToString()
        {
            return getFullName();
        }

        private string[] getBorrowedToolNames()
        {
            List<string> temp = new List<string>();
            foreach (Tool t in getBorrowedTools())
            {
                temp.Add(t.ToString());
            }
            return temp.ToArray();
        }
    }
}
