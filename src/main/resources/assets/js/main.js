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
        .when('/word/:word', {
          templateUrl: '/assets/templates/word.html',
          controller: 'WordController',
          resolve: {
            vocab: ['$http', '$location', '$route', function(
                $http, $location, $route) {
              return $http.get('/api/words/' + $route.current.params.word)
                       .then(function(data) {
                         if (data.status === 'failure') {
                           $location.path('/');
                         } else {
                           return data.data.message;
                         }
                       }, function() {
                         $location.path('/');
                       });
            }]
          }
        })
        .otherwise({
          redirectTo: '/'
        });
    })
    .controller('WordController', ['$scope', 'vocab', function($scope, vocab) {
      document.title = 'ExpressWord - ' + vocab.word;
      $scope.vocab = vocab;
    }])
    .run(['$rootScope', '$route', function($rootScope, $route) {
      $rootScope.$on('$routeChangeSuccess', function() {
        document.title = $route.current.title;
      });
    }]);
})();
