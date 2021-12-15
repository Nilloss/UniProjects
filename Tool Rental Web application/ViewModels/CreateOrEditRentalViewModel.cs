using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using ToolRental.Models;

namespace ToolRental.ViewModels
{
    public class CreateOrEditRentalViewModel
    {
        public IEnumerable<Customer> Customers { get; set; }

        public int CustomerId { get; set; }

        public List<RentedToolViewModel> RentalTools = new List<RentedToolViewModel>();

        public string result { get; set; }

        public DateTime DateRented { get; set; }

        public bool returned { get; set; }
    }
}