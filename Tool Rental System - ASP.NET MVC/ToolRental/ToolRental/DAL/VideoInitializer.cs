using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.Entity;
using ToolRental.Models;

namespace ToolRental.DAL
{
    public class ToolInitializer : DropCreateDatabaseIfModelChanges<ToolContext>
    {
        protected override void Seed(ToolContext context)
        {
            var tools = new List<Tool>
            {
            new Tool{ToolID = 1, Name="App"},
            new Tool{ToolID = 2, Name="Beep"},
            new Tool{ToolID = 3, Name="Cat"}
            };

            tools.ForEach(m => context.Tools.Add(m));
            context.SaveChanges();

            var customers = new List<Customer>()
            {
            new Customer { CustomerId = 1, CustomerName = "John Smith", Phone = "234" },
            new Customer { CustomerId = 2, CustomerName = "Mary Parks", Phone = "13132" },
            new Customer { CustomerId = 3, CustomerName = "Robert Boyd", Phone = "38882" }
            };

            customers.ForEach(c => context.Customers.Add(c));
            context.SaveChanges();


            //base.Seed(context);
            var rentals = new List<Rental>
            {
                new Rental{RentalId = 1, CustomerId = 1, DateRented = DateTime.Parse("01/01/2019"), DateReturned = null },
                new Rental{RentalId = 2, CustomerId = 2, DateRented = DateTime.Parse("01/01/2019"), DateReturned = null },
                new Rental{RentalId = 3, CustomerId = 3, DateRented = DateTime.Parse("01/01/2019"), DateReturned = null }
            };

            rentals.ForEach(r => context.Rentals.Add(r));
            context.SaveChanges();

            var rentalItems = new List<RentalItem>
            {
                new RentalItem{RentalItemId = 1, RentalId = 1, ToolId = 1},
                new RentalItem{RentalItemId = 2, RentalId = 2, ToolId = 2},
                new RentalItem{RentalItemId = 3, RentalId = 3, ToolId = 3}
            };

            rentalItems.ForEach(r => context.RentalItems.Add(r));
            context.SaveChanges();

        }
    }
}