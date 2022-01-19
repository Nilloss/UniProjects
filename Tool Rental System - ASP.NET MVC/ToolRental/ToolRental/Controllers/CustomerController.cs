using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Web;
using System.Web.Mvc;
//using ToolRental.DAL;
using ToolRental.Models;

namespace ToolRental.Controllers
{
    public class CustomerController : Controller
    {
        public ActionResult Index()// right click  Add view - List - Movie, then add <li>@Html.ActionLink("Movies", "Index", "Movie")</li> in to _Layout.cshtml
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync("Customers").Result;

            //We are using IEnumerable because we only want to enumerate the
            //collection and we are not going to add or delete elements
            IEnumerable<Customer> customers = response.Content.ReadAsAsync<IEnumerable<Customer>>().Result;

            return View(customers);
        }


        public ActionResult DisplayCustomer()// right click DisplayTool Add view Empty (without model)
        {
            var customer = new Customer() { CustomerName = "New Customer" };

            return View(customer);
        }

        //private ToolContext db = new ToolContext();

        public ActionResult Details(int Id)
        {

            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Customers/{Id}").Result;
            var customer = response.Content.ReadAsAsync<Customer>().Result;
            return View(customer);
        }
        // Create - Get
        public ActionResult Create()
        {
            return View();
        }
        // Create - Post
        [HttpPost]
        public ActionResult Create(Customer customer)
        {
            try
            {
                HttpResponseMessage response = WebClient.ApiClient.PostAsJsonAsync("Customers", customer).Result;

                // we will refer to this in the Index.cshtml of the Movie so Alertify can display the message
                TempData["SuccessMessage"] = "Customer Added successfully.";

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }
        }

        public ActionResult Edit(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Customers/{Id}").Result;
            var customer = response.Content.ReadAsAsync<Customer>().Result;
            return View(customer);
        }

        [HttpPost]
        public ActionResult Edit(int Id, Customer customer)
        {
            try
            {
                HttpResponseMessage response = WebClient.ApiClient.PutAsJsonAsync($"Customers/{Id}", customer).Result;
                if (response.IsSuccessStatusCode)
                {

                    // we will refer to this in the Index.cshtml of the Movie so Alertify can display the message
                    TempData["SuccessMessage"] = "Saved successfully.";

                    return RedirectToAction("Index");
                }
                return View(customer);
            }
            catch
            {
                return View();
            }
        }

        public ActionResult Delete(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Customers/{Id}").Result;
            var customer = response.Content.ReadAsAsync<Customer>().Result;
            return View(customer);
        }

        [HttpPost]
        public ActionResult Delete(int Id, FormCollection collection)
        {
            try
            {
                HttpResponseMessage response = WebClient.ApiClient.DeleteAsync($"Customers/{Id}").Result;

                // we will refer to this in the Index.cshtml of the Movie so Alertify can display the message
                TempData["SuccessMessage"] = "Customer Deleted successfully.";

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }
        }

        
    }
}