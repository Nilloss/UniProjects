using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Text;

namespace ToolLibrary
{
    class MemberCollection : iMemberCollection
    {
        private Member root;

        public Member getRootNode()
        {
            return root;
        }

        public int Number => throw new NotImplementedException();

        public void add(Member aMember)
        {
            Member member = aMember;

            if (root == null)
            {
                root = member;
            }
            else
            {
                add(member, root);
            }

        }

        private void add(Member member, Member ptr)
        {
            if (member.getFullName().CompareTo(ptr.getFullName()) < 0)
            {
                
                if (ptr.LChild == null)
                    ptr.LChild = member;
                else
                    add(member, ptr.LChild);
            }
            else
            {
                if (ptr.RChild == null)
                    ptr.RChild = member;
                else
                    add(member, ptr.RChild);
            }
        }

        // there are three cases to consider:
        // 1. the node to be deleted is a leaf
        // 2. the node to be deleted has only one child 
        // 3. the node to be deleted has both left and right children

        public void delete(Member aMember)
        {
            // search for item and its parent
            Member ptr = root; // search reference
            Member parent = null; // parent of ptr
            Member member = aMember;
            while ((ptr != null) && (member.getFullName().CompareTo(ptr.getFullName()) != 0))
            {
                parent = ptr;
                if (member.getFullName().CompareTo(ptr.getFullName()) < 0)
                    ptr = ptr.LChild;
                else
                    ptr = ptr.RChild;
            }

            if (ptr != null) // if the search was successful
            {
                // case 3: item has two children
                if ((ptr.LChild != null) && (ptr.RChild != null))
                {
                    // find the right-most node in left subtree of ptr
                    if (ptr.LChild.RChild == null) // a special case: the right subtree of ptr.LChild is empty
                    {
                        ptr = ptr.LChild;
                        //if(ptr.LChild.LChild != null && ptr.LChild != null) ptr.LChild = ptr.LChild.LChild;
                    }
                    else
                    {
                        Member p = ptr.LChild;
                        Member pp = ptr; // parent of p
                        //String ptrName = ptr.FirstName;
                        //String pName = p.FirstName;
                        //String ppName = pp.FirstName;
                        while (p.RChild != null)
                        {
                            pp = p;
                            p = p.RChild;
                        }
                        // copy the item at p to ptr (keep ptr's children, but p's object)

                        assignMemberObject(ptr, p);
                        pp.RChild = p.LChild;
                    }
                }
                else // cases 1 & 2: item has no or only one child
                {
                    Member c;
                    if (ptr.LChild != null)
                        c = ptr.LChild;
                    else
                        c = ptr.RChild;

                    // remove node ptr
                    if (ptr == root) //need to change root
                        root = c;
                    else
                    {
                        if (ptr == parent.LChild)
                            parent.LChild = c;
                        else
                            parent.RChild = c;
                    }
                }
            }
        }


        private void assignMemberObject(Member assignTo, Member assignFrom)
        {
            assignTo.FirstName = assignFrom.FirstName;
            assignTo.LastName = assignFrom.LastName;
            assignTo.PIN = assignFrom.PIN;
            assignTo.ContactNumber = assignFrom.ContactNumber;
        }

        public bool search(Member aMember)
        {
            return search(aMember, root) != null;
        }

        public Member search(Member member, Member parent)
        {
            if (parent != null)
            {
                if (member.getFullName().CompareTo(parent.getFullName()) == 0)
                    return member;
                else
                    if (member.getFullName().CompareTo(parent.getFullName()) < 0)
                    return search(member, parent.LChild);
                else
                    return search(member, parent.RChild);
            }
            return null;
        }

        private int GetMemberArrayIndex(Member member)
        {
            Member[] members = toArray();
            for(int i = 0; i < toArray().Length; i++)
            {
                Member m = members[i];
                if (member.Equals(m))
                {
                    return i;
                }
            }
            return -1;
        }

        public Member RetrieveByName(String fullName)
        {
            foreach(Member m in toArray())
            {
                if (m.getFullName().Equals(fullName))
                {
                    return m;
                }
            }
            return null;
        }

        public Member[] toArray()
        {
            List<Member> temp = new List<Member>();
            temp.Add(PostOrderTraverse(root));
            //return memberList.ToArray();
            return temp.ToArray();
        }

        private Member PostOrderTraverse(Member root)
        {
            if (root != null)
            {
                PostOrderTraverse(root.LChild);
                PostOrderTraverse(root.RChild);
                return root;
                //memberList.Add(root);
            }
            return null;
        }

        public void InOrderTraverse(Member root)
        {
            if (root != null)
            {
                InOrderTraverse(root.LChild);
                Console.WriteLine(root.getFullName());
                InOrderTraverse(root.RChild);
            }
        }
    }
}
