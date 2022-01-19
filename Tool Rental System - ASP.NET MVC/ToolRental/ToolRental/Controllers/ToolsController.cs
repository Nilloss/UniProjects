using System.Collections.Generic;
using System.Web.Mvc;
using ToolRental.Models;
using System.Linq;
//using ToolRental.DAL;

using System.Net.Http;
using System;
using System.Diagnostics;

namespace ToolRental.Controllers
{
    public class ToolController : Controller
    {
        #region Demo Action Method

        // GET: Tool

        //[Route("tool/index/{pageIndex}/{sortBy}")]
        //public ActionResult Index(int? pageIndex, string sortBy)// ? mark here means the parameter does not have to be int, can be null if other data types supplied, string is nullable does not need the ? mark
        //{
        //    //return View();
        //    //return Content("Hello World!");
        //    //return HttpNotFound();
        //    // return RedirectToAction("Contact", "Home");            
        //    if (!pageIndex.HasValue)
        //    {
        //        pageIndex = 1;
        //    }
        //    if (string.IsNullOrWhiteSpace(sortBy))
        //    {
        //        sortBy = "Name";
        //    }

        //    return Content($"PageIndex={pageIndex} and SortBy={sortBy}");
        //    // test /tool/index/5/first
        //}

        //public ActionResult Edit(int? Id)// ? mark here means the parameter does not have to be int, can be null if other data types supplied
        //{
        //    return Content($"The parameter Id is {Id}");
        //}

        #endregion
            


        public ActionResult Index()// right click  Add view - List - Tool, then add <li>@Html.ActionLink("Tools", "Index", "Tool")</li> in to _Layout.cshtml
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync("Tools").Result;

            //We are using IEnumerable because we only want to enumerate the
            //collection and we are not going to add or delete elements
            IEnumerable<Tool> tools = response.Content.ReadAsAsync<IEnumerable<Tool>>().Result;

            //filters: brand,available,condition
            ViewBag.BrandFilter = tools.GroupBy(t => t.Brand)
                   .Select(grp => grp.First()).Select(k => k.Brand)
                   .ToList();

            ViewBag.AvailableTools = GetToolsAvailable().Select(t=>t.ToolID);
            
            return View(tools);
        }

        // this is the Edit get to display the tool details
        public ActionResult Edit(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Tools/{Id}").Result;
            var tools = response.Content.ReadAsAsync<Tool>().Result;

            return View(tools);
        }

        // this is the Edit Post to edit the tool details
        [HttpPost]
        public ActionResult Edit(int Id, Tool tool)
        {
            try
            {
                int VariableToBeWatched = Id;
                HttpResponseMessage response = WebClient.ApiClient.PutAsJsonAsync($"Tools/{Id}", tool).Result;
                if (response.IsSuccessStatusCode)
                {
                    // we will refer to this in the Index.cshtml of the Tool so Alertify can display the message
                    TempData["SuccessMessage"] = "Saved successfully.";

                    return RedirectToAction("Index");
                }
                return View(tool);
            }
            catch
            {
                return View();
            }
        }

        public ActionResult DisplayTool()// right click DisplayTool Add view Empty (without model)
        {
            var tool = new Tool() { Name = "The Avengers" };

            return View(tool);
        }

        //This is the Create Get to display empty tool form
        public ActionResult Create()
        {
            return View();
        }

        //This is the Create Post to create new tool
        [HttpPost]
        public ActionResult Create(Tool tool)
        {
            try
            {
                //Test data
                string[] brands = { "makita", "dewalt", "milwaukee", "ryobi", "bosch", "ridgid" };
                string[] tools = { "Air compressor", "Angle grinder", "Bandsaw", "Chainsaw", "Circular saw", "Drill", "Heat gun", "Leaf blower", "Impact driver", "Rotary tool", "Sander", "Jackhammer", "Lawn mower" };
                string[] comments = { "solid", "powerful", "useless", "durable", "cheap", "inaccurate", "effective" };

                if (true)
                {
                    for (int i = 0; i < 50; i++)
                    {
                        Random r = new Random();
                        Tool k = new Tool();
                        k.Name = tools[r.Next(tools.Length)];
                        k.Brand = brands[r.Next(brands.Length)];
                        k.Comments = comments[r.Next(comments.Length)];
                        k.AssetNumber = r.Next(10000, 999999);
                        k.Active = r.Next(100) < 50 ? true : false;
                        k.Description = "description";
                        HttpResponseMessage responsemessage = WebClient.ApiClient.PostAsJsonAsync("Tools", k).Result;
                    }
                }
                HttpResponseMessage response = WebClient.ApiClient.PostAsJsonAsync("Tools", tool).Result;
                // we will refer to this in the Index.cshtml of the Tool so Alertify can display the message
                TempData["SuccessMessage"] = "Tool Added successfully.";

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }
        }

        public ActionResult Details(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Tools/{Id}").Result;
            var tool = response.Content.ReadAsAsync<Tool>().Result;
            return View(tool);
        }

        //This is the Delete Get to get the tool to be deleted
        public ActionResult Delete(int Id)
        {
            HttpResponseMessage response = WebClient.ApiClient.GetAsync($"Tools/{Id}").Result;
            var tool = response.Content.ReadAsAsync<Tool>().Result;
            return View(tool);
        }

        //This is the Delete Post to get the tool to be deleted
        [HttpPost]
        public ActionResult Delete(FormCollection collection, int Id)
        {
            try
            {
                HttpResponseMessage response = WebClient.ApiClient.DeleteAsync($"Tools/{Id}").Result;

                // we will refer to this in the Index.cshtml of the Tool so Alertify can display the message
                TempData["SuccessMessage"] = "Tool Deleted successfully.";

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
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

            foreach (RentalItem ri in RentalItemsPartOfActiveRentals)
            {
                if (!ActiveRentalItemToolIds.Contains(ri.ToolId))
                {
                    ActiveRentalItemToolIds.Add(ri.ToolId);
                }
            }

            return AllTools.Where(t => !ActiveRentalItemToolIds.Contains(t.ToolID));
        }

        public IEnumerable<Tool> GetTools()
        {
            return WebClient.ApiClient.GetAsync($"Tools").Result.Content.ReadAsAsync<IEnumerable<Tool>>().Result;
        }
    }
}