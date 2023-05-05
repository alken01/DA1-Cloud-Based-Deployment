import os
import requests
import time
import concurrent.futures
import xml.etree.ElementTree as ET
import pickle


# Definition of the functions

def parse_soap_elements(xml_str):
    root = ET.fromstring(xml_str)
    element_dict = {element.tag.split('}')[1]: element.text for element in root.iter() if '}' in element.tag}
    identifier = list(element_dict.keys())[-1]
    return identifier, element_dict[identifier]

def send_rest(url, method, payload=None):
    try:
        if method == "GET":
            response = requests.get(url)
        elif method == "POST":
            response = requests.post(url, json=payload)
        elif method == "PUT":
            response = requests.put(url, json=payload)
        elif method == "DELETE":
            response = requests.delete(url)
        else:
            raise ValueError(f"Invalid method {method}")
    except:
        print(f"Request failed: {url}")
        return None
    return response

def send_soap(url, data):
    headers = {
        'Content-Type': 'text/xml',
        'SOAPAction': '',
    }
    response = requests.post(url, data=data, headers=headers)
    return response

def concurrent_test(dns, port, request_list, num_requests, req='REST'):
    # print server name
    server = f"http://{dns}:{port}"
    country = server.split('.')[1]
    print(f"Server: {country}")
    
    # test every request
    response_times_dict = {}
    for request in request_list: 
        # parse request
        url, method, *args = request
        if req == 'SOAP':
            identifier = parse_soap_elements(method)[0]
        elif req == 'REST':
            identifier = url
        payload = args[0] if args else None
        
        # send requests concurrently
        response_times = []
        with concurrent.futures.ThreadPoolExecutor() as executor:
            for i in range(num_requests):
                # start timer
                start_time = time.perf_counter()
                # send request
                if req == 'REST': send_rest(server + url, method, payload)
                elif req == 'SOAP': send_soap(server + url, payload)
                # end timer
                end_time = time.perf_counter()
                # calculate response time
                response_time = end_time - start_time
                # add response time to list
                response_times.append(response_time)
            # total time rounded to 3 decimal places
            total_time = round(sum(response_times), 3)
            
            if identifier in response_times_dict:
                response_times_dict[identifier] += total_time
            else:
                response_times_dict[identifier] = total_time
            if req == 'REST': 
                print(f"URL: {url}, Method: {method}, Total Response Time: {total_time}")
            elif req == 'SOAP':
                print(f"URL: {identifier}, Method: {parse_soap_elements(method)}, Total Response Time: {total_time}")
    return response_times_dict


# to save the variables (server)
def save_dict_as_pickle_file(dictionary, filename):
    with open(filename, 'wb') as f:
        pickle.dump(dictionary, f)



def main():
    server_list = [
        ('alnike.japaneast.cloudapp.azure.com', '20.210.110.130'),
        ('uswest-thiers.westus3.cloudapp.azure.com', '20.106.100.68'),
        ('useast-lennart.eastus2.cloudapp.azure.com', '20.1.139.66'), 
        ('dapps.westeurope.cloudapp.azure.com', '98.71.185.120')
    ]

    num_requests = 10
    this_ip = requests.get('https://api.ipify.org').text

    rest_port = 8081
    rest_request_list = [
        ['/rest/order', 'POST', {'address': '123 Main St', 'meals': ['Fish and Chips', 'Steak']}],
        ['/rest/meals', 'GET'],
        ['/rest/largest-meal', 'GET'],
        ['/rest/cheapest-meal', 'GET']
    ]

    rest_response_times_dict = {}

    for dns, ip in server_list:
        # if this is the current server, skip
        if ip == this_ip: continue
        
        # test REST
        response_times = concurrent_test(dns, rest_port, rest_request_list, num_requests, req='REST')
        
        for url, times in response_times.items():
            if url not in rest_response_times_dict:
                rest_response_times_dict[url] = {}
            rest_response_times_dict[url][dns] = times
        print('\n')

    # save the variables
    save_dict_as_pickle_file(rest_response_times_dict, 'rest_response_times_dict.pickle')


    # read the soap requests from the directory
    soap_request_list = []
    soap_dir = 'soap_requests'
    for filename in os.listdir(soap_dir):
        with open(os.path.join(soap_dir, filename), 'r') as f:
            data = f.read()
            soap_request_list.append(['/ws', data.strip()])

    soap_port = 8080

    # call concurrent_test() with soap_request_list
    soap_response_times_dict = {}
    for dns, ip in server_list:
        # if this is the current server, skip
        if ip == this_ip: continue

        # test REST or SOAP
        response_times = concurrent_test(dns, soap_port, soap_request_list, num_requests, req='SOAP')
        
        for url, times in response_times.items():
            if url not in soap_response_times_dict:
                soap_response_times_dict[url] = {}
            soap_response_times_dict[url][dns] = times
        print('\n')

    # save the variables
    save_dict_as_pickle_file(soap_response_times_dict, 'soap_response_times_dict.pickle')

if __name__ == "__main__":
    main()
