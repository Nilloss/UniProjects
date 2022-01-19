using KellermanSoftware.CompareNetObjects;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net.Http;
using System.Reflection;
using System.Web;
using System.Web.Mvc;
using ToolRental.Models;
using ToolRental.ViewModels;

namespace ToolRental.Controllers
{
    public class RentalsController : Controller
    {

        #region Index

        public ActionResult Index()
        {

            RentalsIndexViewModel rentalsIndexViewModel = new RentalsIndexViewModel();

            TempData["RentalData"] = null;
            TempData["AddRentalData"] = null;
            TempData["EditRental"] = null;
            TempData["EditRentalId"] = null;

            HttpResponseMessage response = WebClient.ApiClient.GetAsync("Rentals").Result;

            IEnumerable<CustomerRentalsViewModel> CustomerRentalsList = response.Content.ReadAsAsync<IEnumerable<CustomerRentalsViewModel>>().Result;

            rentalsIndexViewModel.CustomerRentalsList = CustomerRentalsList;

            rentalsIndexViewModel.CompletedRentals = CustomerRentalsList.Where(c => c.DateReturned != null);

            //Populate rentals view model with customer names according to id
            IEnumerable<Customer> customers = GetCustomers();
            foreach (CustomerRentalsViewModel crvm in CustomerRentalsList)
            {
                crvm.CustomerName = customers.Where(c => c.CustomerId == crvm.CustomerId).Select(c => c.CustomerName).FirstOrDefault();
            }

            return View(rentalsIndexViewModel);
        }

        #endregion

        #region Create
        //Create - Get
        public ActionResult Create()
        {
            object Temp = GetTempData("RentalData");

            Rental rental = new Rental();
            
            //If tempdata contains more data than a new Rental object, then set the above instance to tempdata, else set tempdata to the new instance above
            if (!(Temp == new Rental()) && !(Temp == null)){
                rental = (Rental)GetTempData("RentalData");
            }
            else
            {
                TempData["RentalData"] = rental;
            }

            CreateOrEditRentalViewModel createOrEditRentalViewModel = new CreateOrEditRentalViewModel();

            //Customers drop down
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Customers").Result;
            IEnumerable<Customer> Customers = response.Content.ReadAsAsync<IEnumerable<Customer>>().Result;
            createOrEditRentalViewModel.Customers = Customers;

            //Date Rented field
            createOrEditRentalViewModel.DateRented = DateTime.Now;

            //RentalTools display (RentedToolViewModel)
            IEnumerable<Tool> AllTools = GetTools();
            foreach (RentalItem ri in rental.RentalItems)
            {
                createOrEditRentalViewModel.RentalTools.Add(new RentedToolViewModel() { ToolId = ri.ToolId, ToolName = AllTools.Where(t => t.ToolID == ri.ToolId).FirstOrDefault().Name });
            }

            return View(createOrEditRentalViewModel);
        }

        //Create - Post
        [HttpPost]
        public ActionResult Create(CreateOrEditRentalViewModel createOrEditRentalViewModel)
        {
            try
            {
                Rental rental = (Rental)GetTempData("RentalData");
                rental.CustomerId = Convert.ToInt32(createOrEditRentalViewModel.result);
                rental.DateRented = createOrEditRentalViewModel.DateRented;

                HttpResponseMessage PostRental = WebClient.ApiClient.PostAsJsonAsync("Rentals", rental).Result;

                TempData["SuccessMessage"] = "Rental Added successfully.";
                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }

        }
        #endregion

        #region Edit
        // Edit - Get
        public ActionResult Edit(int Id)
        {
            TempData["EditRental"] = true;
            TempData["EditRentalId"] = Id;

            HttpResponseMessage RentalsResponse = WebClient.ApiClient.GetAsync($"Rentals/{Id}").Result;
            HttpResponseMessage RentalItemsResponse = WebClient.ApiClient.GetAsync($"RentalItems").Result;

            IEnumerable<RentalItem> AllRentalItems =  RentalItemsResponse.Content.ReadAsAsync<IEnumerable<RentalItem>>().Result;
            IEnumerable<Tool> AllTools = GetTools();

            var rental = RentalsResponse.Content.ReadAsAsync<Rental>().Result;

            object Temp = GetTempData("RentalData");

            if(Temp != null)
            {
                rental = (Rental)GetTempData("RentalData");
            }
            else
            {
                TempData["RentalData"] = rental;
            }

            CreateOrEditRentalViewModel createOrEditRentalViewModel = new CreateOrEditRentalViewModel();

            if (rental.DateReturned != null)
            {
                createOrEditRentalViewModel.returned = true;
            }

            IEnumerable<RentalItem> RentalItemsForSaidRental = GetTempData("EditRental") == null ? AllRentalItems.Where(r => r.RentalId == rental.RentalId).ToList() : rental.RentalItems;


            createOrEditRentalViewModel.DateRented = rental.DateRented;
            createOrEditRentalViewModel.Customers = GetCustomers();
            createOrEditRentalViewModel.CustomerId = rental.CustomerId;
            
            List<RentedToolViewModel> rentedToolsDisplayList = new List<RentedToolViewModel>();
            
            foreach (RentalItem ri in RentalItemsForSaidRental)
            {
                try
                {
                    rentedToolsDisplayList.Add(new RentedToolViewModel() { ToolId = ri.ToolId, ToolName = AllTools.Where(t => ri.ToolId == t.ToolID).FirstOrDefault().Name });
                }
                catch
                {
                }
            }

            createOrEditRentalViewModel.RentalTools = rentedToolsDisplayList;

            return View(createOrEditRentalViewModel);
        }

        // Edit - Post
        [HttpPost]
        public ActionResult Edit(int Id, CreateOrEditRentalViewModel createOrEditRentalViewModel)
        {

            try
            {
                Rental rental = (Rental)GetTempData("RentalData");
                rental.CustomerId = Convert.ToInt32(createOrEditRentalViewModel.result);
                rental.DateRented = createOrEditRentalViewModel.DateRented;

                HttpResponseMessage PostRental = WebClient.ApiClient.PutAsJsonAsync($"Rentals/{Id}", rental).Result;

                IEnumerable<RentalItem> AllRentalItems = WebClient.ApiClient.GetAsync($"RentalItems").Result.Content.ReadAsAsync<IEnumerable<RentalItem>>().Result;
                IEnumerable<RentalItem> AllRentalItemsWithThisRentalId = AllRentalItems.Where(r => r.RentalId == rental.RentalId);

                foreach(RentalItem ri in AllRentalItemsWithThisRentalId)
                {
                    HttpResponseMessage response = WebClient.ApiClient.DeleteAsync($"RentalItems/{ri.RentalItemId}").Result;
                }

                foreach (RentalItem ri in rental.RentalItems)
                {
                    HttpResponseMessage PostRentalItem = WebClient.ApiClient.PostAsJsonAsync("RentalItems", ri).Result;
                }

                TempData["SuccessMessage"] = "Rental Updated successfully.";
                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }

            //Rental rental = new Rental();
            //rental.CustomerId = Convert.ToInt32(createOrEditRentalViewModel.result);
            //rental.DateRented = createOrEditRentalViewModel.DateRented;
            //rental.RentalId = Id;

            //try
            //{
            //    HttpResponseMessage response = WebClient.ApiClient.PutAsJsonAsync($"Rentals/{Id}", rental).Result;
            //    if (response.IsSuccessStatusCode)
            //    {
            //        TempData["SuccessMessage"] = "Saved successfully.";

            //        return RedirectToAction("Index");
            //    }
            //    return View(createOrEditRentalViewModel);
            //}
            //catch
            //{
            //    return View();
            //}
        }
        #endregion

        #region Details
        public ActionResult Details(int Id)
        {
            CustomerRentalDetailsViewModel customerRentalDetailsViewModel = new CustomerRentalDetailsViewModel();
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Rentals/{Id}").Result;
            Rental rental = response.Content.ReadAsAsync<Rental>().Result;
            customerRentalDetailsViewModel.RentalId = rental.RentalId;
            customerRentalDetailsViewModel.DateRented = rental.DateRented;
            customerRentalDetailsViewModel.CustomerName = GetCustomers().Where(c => c.CustomerId == rental.CustomerId).FirstOrDefault().CustomerName;

            //RentalTools display (RentedToolViewModel)
            IEnumerable<Tool> AllTools = GetTools();
            List<RentedToolViewModel> DisplayTools = new List<RentedToolViewModel>();

            foreach (RentalItem ri in GetRentalItems()) 
            {
                if(ri.RentalId == rental.RentalId)
                {
                    try
                    {
                        DisplayTools.Add(new RentedToolViewModel() { ToolId = ri.ToolId, ToolName = AllTools.Where(t => t.ToolID == ri.ToolId).FirstOrDefault().Name });
                    }
                    catch
                    {
                        Debug.WriteLine("Doing nothing");
                    }
                }
            }
            customerRentalDetailsViewModel.RentalTools = DisplayTools;
            return View(customerRentalDetailsViewModel);
        }
        #endregion

        #region Delete
        //Delete - Get
        public ActionResult Delete(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Rentals/{Id}").Result;
            var rental = response.Content.ReadAsAsync<Rental>().Result;

            try
            {
                ViewBag.CustomerName = GetCustomers().Where(c => c.CustomerId == rental.CustomerId).FirstOrDefault().CustomerName;
            }
            catch
            {

            }

            return View(rental);
        }

        // Delete - Post
        [HttpPost]
        public ActionResult Delete(int Id, FormCollection collection)
        {
            try
            {
                HttpResponseMessage response = WebClient.ApiClient.DeleteAsync($"Rentals/{Id}").Result;

                // we will refer to this in the Index.cshtml of the Tool so Alertify can display the message
                TempData["SuccessMessage"] = "Rental Deleted successfully.";

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }
        }
        #endregion

        #region Return

        // Return get
        public ActionResult Return(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Rentals/{Id}").Result;
            var rental = response.Content.ReadAsAsync<Rental>().Result;

            ViewBag.CustomerName = GetCustomers().Where(c => c.CustomerId == rental.CustomerId).FirstOrDefault().CustomerName;
            rental.DateReturned = DateTime.Now;

            return View(rental);
        }

        // Return post
        [HttpPost]
        public ActionResult Return(int Id, Rental rental)
        {
            try
            {
                HttpResponseMessage getResponse = WebClient.ApiClient.GetAsync($"Rentals/{Id}").Result;
                rental = getResponse.Content.ReadAsAsync<Rental>().Result;
                rental.DateReturned = DateTime.Now;
                HttpResponseMessage response = WebClient.ApiClient.PutAsJsonAsync($"Rentals/{Id}", rental).Result;
                if (response.IsSuccessStatusCode)
                {
                    TempData["SuccessMessage"] = "Saved successfully.";

                    return RedirectToAction("Index");
                }
                return View(rental);
            }
            catch
            {
                return View();
            }
        }


        #endregion

        #region Tools Subform

        #region Add
        // AddTools - Get
        public ActionResult AddTools()
        {

            AddRentalViewModel addRentalViewModel = new AddRentalViewModel();

            Rental rental = (Rental)GetTempData("RentalData");

            object temp = GetTempData("EditRental");
            if (temp != null)
            {
                var addrentaldata = (AddRentalViewModel)GetTempData("AddRentalData");
                if (addrentaldata == null)
                {
                    addRentalViewModel.ToolsAvailable = GetToolsAvailable();
                    TempData["AddRentalData"] = addRentalViewModel;
                }
                else
                {
                    addRentalViewModel = addrentaldata;
                }
            }
            else
            {
                if (rental.RentalItems.Count == 0)
                {
                    addRentalViewModel.ToolsAvailable = GetToolsAvailable();
                    TempData["AddRentalData"] = addRentalViewModel;
                }
                else
                {
                    addRentalViewModel = (AddRentalViewModel)GetTempData("AddRentalData");
                }
            }

            return View(addRentalViewModel);
        }

        // AddTools - Post
        [HttpPost]
        public ActionResult AddTools(AddRentalViewModel addrentalviewmodel)
        {

            Rental rental = (Rental)GetTempData("RentalData");

            RentalItem rentalItem = new RentalItem();
            
            rentalItem.ToolId = Convert.ToInt32(addrentalviewmodel.result);

            addrentalviewmodel = (AddRentalViewModel)GetTempData("AddRentalData");

            addrentalviewmodel.ToolsAvailable = addrentalviewmodel.ToolsAvailable.Where(t => t.ToolID != rentalItem.ToolId).ToList();

            TempData["AddRentalData"] = addrentalviewmodel;
            
            if(rentalItem.ToolId != 0)
            {
                rental.RentalItems.Add(rentalItem);
            }

            int rentalId = Convert.ToInt32(GetTempData("EditRentalId"));

            object temp = GetTempData("EditRental");
            if (temp != null)
            {
                return RedirectToAction("Edit", new { id = rentalId });
            }
            else
            {
                return RedirectToAction("Create");
            }
        }
        #endregion
        
        #region Edit
        // EditRentedTool - Get
        public ActionResult EditRentedTool(int Id)
        {
            //var rentalItem = db.RentalItems.Single(ri => ri.RentalItemId == Id);
            //rentalItem.Tools = GetTools();

            return View();
        }

        // EditRentedTool - Post
        [HttpPost]
        public ActionResult EditRentedTool(int Id, FormCollection collection)
        {
            try
            {
                //var rentalItem = db.RentalItems.Single(ri => ri.RentalItemId == Id);
                //if (TryUpdateModel(rentalItem))
                //{
                //db.SaveChanges();
                //    return RedirectToAction("Edit", new { Id = rentalItem.RentalId });
                //}
                //return View(rentalItem);
                return View();
            }
            catch
            {
                return View();
            }
        }
        #endregion

        #region Delete
        // DeleteRentedTool - Get
        public ActionResult DeleteRentedTool(int Id)
        {
            Tool tool = GetTools().Where(t => t.ToolID == Id).FirstOrDefault();
            return View(tool);
        }

        [HttpPost]
        public ActionResult DeleteRentedTool(Tool tool, int Id)
        {
            Rental rental = (Rental)GetTempData("RentalData");
            RentalItem r = rental.RentalItems.Where(k => k.ToolId == Id).FirstOrDefault();
            rental.RentalItems.Remove(r);
            return RedirectToAction("Edit", new { id = rental.RentalId });
        }
        #endregion

        #endregion

        #region Helper Method

        private void PrintObject(object obj)
        {
            Type t = obj.GetType(); // Where obj is object whose properties you need.
            PropertyInfo[] pi = t.GetProperties();
            foreach (PropertyInfo p in pi)
            {
                Debug.Print(p.Name + " : " + p.GetType());
            }
        }

        private IEnumerable<Tool> GetToolsAvailable()
        {
            //Available tools = alltools - ids in rental items that are part of active rentals

            IEnumerable<Rental> AllRentals = WebClient.ApiClient.GetAsync($"Rentals").Result.Content.ReadAsAsync<IEnumerable<Rental>>().Result;
            IEnumerable<Rental> ActiveRentals = AllRentals.Where(r => r.DateReturned == null);
            IEnumerable<int> ActiveRentalIds = ActiveRentals.Select(s => s.RentalId);

            IEnumerable<RentalItem> AllRentalItems = WebClient.ApiClient.GetAsync($"RentalItems").Result.Content.ReadAsAsync<IEnumerable<RentalItem>>().Result;
            IEnumerable<RentalItem> RentalItemsPartOfActiveRentals = AllRentalItems.Where(a => ActiveRentalIds.Contains(a.RentalId));

            IEnumerable<Tool> AllTools = GetTools();

            List<int> ActiveRentalItemToolIds = new List<int>();

            foreach(RentalItem ri in RentalItemsPartOfActiveRentals)
            {
                if (!ActiveRentalItemToolIds.Contains(ri.ToolId))
                {
                    ActiveRentalItemToolIds.Add(ri.ToolId);
                }
            }

            return AllTools.Where(t=>!ActiveRentalItemToolIds.Contains(t.ToolID));
        }

        public IEnumerable<Customer> GetCustomers()
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Customers").Result;
            return response.Content.ReadAsAsync<IEnumerable<Customer>>().Result;
        }

        public IEnumerable<RentalItem> GetRentalItems()
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"RentalItems").Result;
            return response.Content.ReadAsAsync<IEnumerable<RentalItem>>().Result;
        }

        public IEnumerable<Tool> GetTools()
        {
            return WebClient.ApiClient.GetAsync($"Tools").Result.Content.ReadAsAsync<IEnumerable<Tool>>().Result;
        }

        private object GetTempData(string key)
        {
            object obj = TempData[key];
            TempData.Keep(key);
            return obj;
        }

        #endregion
    }
}