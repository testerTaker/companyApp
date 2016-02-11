function CompanyService( $http){
    this._http = $http;
}
CompanyService.prototype.getCompanies = function() {
    return this._http({
          method: 'GET',
          url: '/company'
    });

};
CompanyService.prototype.getCompany = function(companyId) {
    return this._http({
          method: 'GET',
          url: '/company/'+companyId
    });
};
CompanyService.prototype.saveCompany = function(data) {
    var config = {headers: {'Content-Type': 'application/json'} };
    if(!!data.id){
        return this._http.put('/company/'+data.id,data,config);
    }
    else{
        return this._http.post('/company',data,config);
    }
};
function EmployeeService($http){
    this._http = $http;
}

EmployeeService.prototype.getEmployees = function(companyId) {
    return this._http({
          method: 'GET',
          url: '/company/'+companyId+'/employees'
    });

};

EmployeeService.prototype.getEmployee = function(employeeId) {
    return this._http({
          method: 'GET',
          url: '/employee/'+employeeId
    });

};
EmployeeService.prototype.saveEmployee =  function(companyId, data) {
    var config = {headers: {'Content-Type': 'application/json'} };
    if(!!data.id){
        return this._http.put('/employee/'+data.id,data,config);
    }
    else{
        return this._http.post('/company/'+companyId+'/employee',data,config);
    }
};
function CompanyListController(companyService){
    var ctrl = this;
    ctrl.fetching = true;
    companyService.getCompanies().then(function(response){
        ctrl.fetching = false;
        if(response.data.success===false){
            ctrl.has_errors = true;
            return;            
        }
        ctrl.companies = response.data;
    })
    .catch(function(){
        ctrl.fetching = false;
        ctrl.has_errors = true;

    })

}
function EmployeeListController(employeeService, $routeParams){
    this._employeeService = employeeService;
    this._routeParams = $routeParams;
    var companyId = $routeParams.companyId;
    var ctrl = this;
    ctrl.employees = {};
    ctrl.fetching = true;
    this._employeeService.getEmployees(companyId).then(function(response){
        ctrl.fetching = false;
        if(response.data.success===false){
            ctrl.has_errors = true;
            return;            
        }
        ctrl.employees = response.data;
    })
    .catch(function(){
        ctrl.fetching = false;
        ctrl.has_errors = true;
    })
}
function EmployeeFormController(employeeService,$routeParams){
    this._employeeService = employeeService;
    this._routeParams = $routeParams;
    this._companyId = $routeParams.companyId; 
    var employeeId = $routeParams.employeeId;
    var ctrl = this;
    this.employee = {};
    if(employeeId){
        ctrl.processing = true;
        ctrl.fetching = true;
        this._employeeService.getEmployee(employeeId).then(function(response){
            ctrl.fetching = false;
            ctrl.processing = false;
            if(response.data.success===false){
                ctrl.has_errors = true;
                return;            
            }
            ctrl.employee = response.data;
            ctrl._companyId = ctrl.employee.company_id;
        })
        .catch(function(){
            ctrl.processing = false;
            ctrl.fetching = false;
            ctrl.has_errors = true;
        })

    }
}
EmployeeFormController.prototype.save_form = function(isValid) {
    var ctrl = this;
    ctrl.processing = true;
    ctrl.has_errors = false;
    ctrl.saved_with_success = false;
    if(!isValid) return false;
    this._employeeService.saveEmployee(this._companyId,this.employee).then(function(response){
        ctrl.processing = false;
        if(response.data.success===false){
            ctrl.has_errors = true;
            return;            
        }
        ctrl.saved_with_success = true;
        ctrl.employee.id = response.data.id;

    })
    .catch(function(){
        ctrl.processing = false;
        ctrl.has_errors = true;
    })
    return false;
}
function CompanyFormController(companyService, $routeParams){
    this._companyService = companyService;
    this._routeParams = $routeParams;
    this.company = {};
    var companyId = $routeParams.companyId;
    var ctrl = this;
    if(companyId){
        ctrl.processing = true;
        ctrl.fetching = true;
        this._companyService.getCompany(companyId).then(function(response){
            ctrl.fetching = false;
            ctrl.processing = false;
            if(response.data.success===false){
                ctrl.has_errors = true;
                return;            
            }
            ctrl.company = response.data;
        })
        .catch(function(){
            ctrl.processing = false;
            ctrl.fetching = false;
            ctrl.has_errors = true;
        })

    }
}
CompanyFormController.prototype.save_form = function(isValid) {
    var ctrl = this;
    ctrl.processing = true;
    ctrl.has_errors = false;
    ctrl.saved_with_success = false;
    if(!isValid) return false;
    this._companyService.saveCompany(this.company).then(function(response){
        ctrl.processing = false;
        if(response.data.success===false){
            ctrl.has_errors = true;
            return;            
        }
        ctrl.saved_with_success = true;
        ctrl.company.id = response.data.id;

    })
    .catch(function(){
        ctrl.processing = false;
        ctrl.has_errors = true;
    })
    return false;
};
var app = angular.module('company-app',['ngRoute'])
.service('companyService',['$http', CompanyService])
.service('employeeService',['$http', EmployeeService])

.directive('companyList',[function(){
    return {
        templateUrl:'/templates/companyList.html',
        restrict: 'E',
        controllerAs:'ctrl',
        controller:['companyService',CompanyListController]
    };
}])
.directive('employeeList',[function(){
    return {
        templateUrl:'/templates/employeeList.html',
        restrict: 'E',
        controllerAs:'ctrl',
        controller:['employeeService','$routeParams',EmployeeListController]
    };
}])
.directive('companyForm',[function(){
    return {
        templateUrl:'/templates/companyForm.html',
        restrict: 'E',
        controllerAs:'ctrl',
        controller:['companyService','$routeParams',CompanyFormController]
    };
}])
.directive('employeeForm',[function(){
    return {
        templateUrl:'/templates/employeeForm.html',
        restrict: 'E',
        controllerAs:'ctrl',
        controller:['employeeService','$routeParams',EmployeeFormController]
    };
}])
app.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider
        .when('/companies', {
          templateUrl: 'templates/companyPage.html'
        })
        .when('/createCompany', {
         templateUrl: 'templates/createCompanyPage.html'
        })
        .when('/editCompany/:companyId', {
            templateUrl: 'templates/editCompanyPage.html'
        })
        .when('/editEmployee/:employeeId', {
            templateUrl: 'templates/editEmployeePage.html'
        })
        .when('/listEmployees/:companyId', {
          templateUrl: 'templates/listEmployeePage.html',
          controller:['$routeParams','$scope', function($routeParams, $scope){
                $scope.companyId = $routeParams.companyId;
          }]

        })    
        .when('/addEmployee/:companyId', {
          templateUrl: 'templates/addEmployeePage.html',
        })    
        .otherwise({
        redirectTo: '/companies'
        });
  }]);