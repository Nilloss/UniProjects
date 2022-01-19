using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ToolRental.ViewModels
{
    public class CustomerRentalsViewModel
    {
        public int RentalId { get; set; }
        public int CustomerId { get; set; }
        public DateTime DateRented { get; set; }

        public DateTime? DateReturned { get; set; }

        public string CustomerName { get; set; }
        
    }
}