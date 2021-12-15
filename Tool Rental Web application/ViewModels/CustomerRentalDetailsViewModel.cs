using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using ToolRental.Models;

namespace ToolRental.ViewModels
{
    public class CustomerRentalDetailsViewModel
    {
        public int RentalId { get; set; }
        public string CustomerName { get; set; }
        public DateTime DateRented { get; set; }
        public List<RentedToolViewModel> RentalTools { get; set; }
    }
}