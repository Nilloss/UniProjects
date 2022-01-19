using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace ToolRental.Models
{
    public class RentalItem
    {
        public int RentalItemId { get; set; }
        public int RentalId { get; set; }
        public int ToolId { get; set; }
    }
}