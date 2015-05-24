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
        .when('/search', {
          templateUrl: '/assets/templates/search.html',
          controller: 'SearchResultsController',
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
      var exactMatchRegex = /^"(.*)"$/
      $scope.search = function() {
        if (exactMatchRegex.test($scope.query)) {
          $location.path('/word/' +
            $scope.query.replace(exactMatchRegex, '$1'));
        } else {
          $location.path('/search').search('query', $scope.query);
        }
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
        word: '',
        definitions: [{ text: '', category: 'Part of Speech' }],
        sentences: ['']
      };
      $scope.entry = angular.copy(defaultEntry);
      $scope.partsOfSpeech = ['Noun', 'Pronoun', 'Verb', 'Adjective',
        'Adverb', 'Participle', 'Article', 'Preposition'];
      $scope.setPoS = function(p) {
        $scope.entry.definitions[0].category = p;
      };
      $scope.validateInput = function() {
        var valid = $scope.entry.definitions[0].category !== 'Part of Speech' &&
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
    .controller('SearchResultsController', ['$scope', '$routeParams', '$http',
        function($scope, $routeParams, $http) {
      $scope.query = $routeParams.query ? $routeParams.query : '';
      document.title = 'ExpressWord - Search: ' + $scope.query;
      $http.get('/api/words?search=' + $scope.query)
        .success(function(response) { $scope.results = response.message; });
    }])
    .run(['$rootScope', '$route', function($rootScope, $route) {
      $rootScope.$on('$routeChangeSuccess', function() {
        document.title = $route.current.title;
      });
    }]);
})();
