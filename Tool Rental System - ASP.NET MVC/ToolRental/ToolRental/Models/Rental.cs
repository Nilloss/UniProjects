using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using ToolRental.ViewModels;

namespace ToolRental.Models
{
    public class Rental
    {
        public int RentalId { get; set; }

        public int CustomerId { get; set; }
        
        public DateTime DateRented { get; set; }

        public DateTime? DateReturned { get; set; }

        public List<RentalItem> RentalItems = new List<RentalItem>();
    }
}