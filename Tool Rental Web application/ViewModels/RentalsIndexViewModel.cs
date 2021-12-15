using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using ToolRental.Models;

namespace ToolRental.ViewModels
{
    public class RentalsIndexViewModel
    {
        public IEnumerable<CustomerRentalsViewModel> CustomerRentalsList { get; set; }

        public IEnumerable<CustomerRentalsViewModel> CompletedRentals { get; set; }
    }
}