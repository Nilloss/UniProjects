using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Web;

namespace ToolRental
{
    public static class WebClient
    {
        public static HttpClient ApiClient = new HttpClient();

        static WebClient()
        {
            ApiClient.BaseAddress = new Uri(System.Configuration.ConfigurationManager.AppSettings["WebApiUrl"]);
            ApiClient.DefaultRequestHeaders.Clear();
            ApiClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
        }
    }
}