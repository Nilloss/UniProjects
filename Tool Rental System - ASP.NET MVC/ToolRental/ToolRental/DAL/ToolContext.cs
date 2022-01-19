using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.ModelConfiguration.Conventions;
using System.Linq;
using System.Web;
using ToolRental.Models;

namespace ToolRental.DAL
{
    public class ToolContext : DbContext
    {
        public DbSet<Tool> Tools { get; set; }
        public DbSet<Customer> Customers { get; set; }
        public DbSet<Rental> Rentals { get; set; }
        public DbSet<RentalItem> RentalItems { get; set; }

        // to fix the error of EF not firing the ToolInitializer,
        // create this constructor and force the ToolInitializer to fire.
        // Also, in the ToolInitializer, change the Instance from
        // DropCreateDatabaseIfModelChanges to DropCreateDatabaseAlways

        //public ToolContext() : base("ToolContext")
        //{
        //    Database.SetInitializer(new ToolInitializer());
        //}

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Conventions.Remove<PluralizingTableNameConvention>();
        }
    }
}