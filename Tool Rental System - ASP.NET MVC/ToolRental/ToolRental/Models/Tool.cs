using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ToolRental.Models
{
    public class Tool
    {
        public int ToolID { get; set; }

        public string Name { get; set; }

        public string Description { get; set; }

        public int AssetNumber { get; set; }

        public string Brand { get; set; }

        public bool Active { get; set; }

        public string Comments { get; set; }
    }
}