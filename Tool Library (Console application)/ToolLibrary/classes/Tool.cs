using System;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class Tool : iTool
    {
        private string name;
        private int quantity;
        private CATEGORY category;
        private string type;
        private int noBorrowings;

        public Tool(string Name, int Qty, CATEGORY Category, string Type)
        {
            this.name = Name;
            this.quantity = Qty;
            this.category = Category;
            this.type = Type;
        }

        public Tool()
        {
        }

        public string Name { get => name; set => name = value; }
        public int Quantity { get => quantity; set => quantity = value; }
        public int AvailableQuantity { get => quantity - NoBorrowings; set => throw new NotImplementedException();/*No reason to set this manually*/ }
        public int NoBorrowings { get => Program.testing ? noBorrowings : getNoBorrowings(); set => noBorrowings = value;}

        public CATEGORY Category { get => category; set => category = value; }

        public string Type { get => type; set => type = value; }

        public MemberCollection GetBorrowers => getBorrowers();

        public void addBorrower(Member aMember) //Leaving unimplemented
        {
            throw new NotImplementedException();
        }

        public void deleteBorrower(Member aMember) //Leaving unimplemented
        {
            throw new NotImplementedException();
        }

        public override string ToString()
        {
            return name;
        }
        private MemberCollection getBorrowers()
        {
            MemberCollection borrowers = new MemberCollection();
            foreach (Member m in Program.library.memberCollection.toArray())
            {
                if(m != null)
                {
                    foreach (Tool t in m.getBorrowedTools())
                    {
                        if (t.Name.Equals(Name))
                        {
                            if (!borrowers.search(m)) borrowers.add(m);
                        }
                    }
                }
            }
            return borrowers;
        }

        private int getNoBorrowings()
        {
            int count = 0;
            foreach(Member m in Program.library.memberCollection.toArray()){
                if(m != null)
                {
                    foreach (Tool t in m.getBorrowedTools())
                    {
                        if (t.Equals(this)) count++;
                    }
                }
            }
            return count;
        }
    }
}
