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
                       .then(function(result) {
                         var data = result.data
                         if (data.status == 'failure') {
                           $location.path('/');
                         } else {
                           return data.message;
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
      if (vocab === undefined) return;
      document.title = 'ExpressWord - ' + vocab.word;
      $scope.vocab = vocab;
    }])
    .directive('searchBox', function() {
      return {
        scope: {},
        restrict: 'E',
        controller: 'SearchController',
        templateUrl: '/assets/templates/searchBox.html'
      }
    })
    .controller('SearchController', ['$scope', '$location',
        function($scope, $location) {
      $scope.search = function() {
        $location.path('/word/' + $scope.query);
      };
    }])
    .directive('wordUpdatePanel', function() {
      return {
        scope: {},
        restrict: 'E',
        controller: 'WordUpdateCtrl',
        templateUrl: '/assets/templates/wordUpdatePanel.html'
      }
    })
    .controller('WordUpdateCtrl', ['$scope', '$http', function($scope, $http) {
      var defaultEntry = {
        word: "",
        definitions: [{ text: "", category: "Part of Speech" }],
        sentences: [""]
      };
      $scope.entry = angular.copy(defaultEntry);
      $scope.partsOfSpeech = ['Noun', 'Pronoun', 'Verb', 'Adjective',
        'Adverb', 'Participle', 'Article', 'Preposition'];
      $scope.setPoS = function(p) {
        $scope.entry.definitions[0].category = p;
      };
      $scope.validateInput = function() {
        var valid = $scope.entry.definitions[0].category !== "Part of Speech" &&
          $scope.entry.definitions[0].text.trim()  &&
          $scope.entry.word.trim() && $scope.entry.sentences[0].trim();
        return valid;
      };
      $scope.submitting = false;
      $scope.submitEntry = function() {
        if (!$scope.submitting && $scope.validateInput()) {
          $scope.entry.aggregate = [];
          $http.post('/api/words', $scope.entry).success(function() {
            $scope.entry = angular.copy(defaultEntry);
          });
        }
      };
    }])
    .run(['$rootScope', '$route', function($rootScope, $route) {
      $rootScope.$on('$routeChangeSuccess', function() {
        document.title = $route.current.title;
      });
    }]);
})();
