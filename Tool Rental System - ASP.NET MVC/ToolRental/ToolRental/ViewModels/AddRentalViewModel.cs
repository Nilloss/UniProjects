using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using ToolRental.Models;

namespace ToolRental.ViewModels
{
    public class AddRentalViewModel
    {
        public IEnumerable<Tool> ToolsAvailable { get; set; }
        public string result { get; set; }
    }
}