(function() {
  angular.module('expressWordApp', ['ngRoute', 'ui.bootstrap'])
    .config(function($locationProvider, $routeProvider) {
      $locationProvider.html5Mode(true);
      $routeProvider
        .when('/', {
          title: 'ExpressWord',
          templateUrl: '/assets/templates/index.html'
        })
        .when('/about', {
          title: 'ExpressWord - About',
          templateUrl: '/assets/templates/about.html'
        })
        .otherwise({
          redirectTo: '/'
        });
    })
    .run(['$rootScope', '$route', function($rootScope, $route) {
      $rootScope.$on('$routeChangeSuccess', function() {
        document.title = $route.current.title;
      });
    }]);
})();
