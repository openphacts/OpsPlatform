LSP4All::Application.routes.draw do
  
  get "home/index", :as => :home
  
  resources :users
  resources :application_modules
  
  get    'login(.:format)'  => 'user_sessions#new',     :as => :login
  post   'login(.:format)'  => 'user_sessions#create',  :as => :login
  delete 'logout(.:format)' => 'user_sessions#destroy', :as => :logout
  # Extjs not very restfull by default  
  get    'login_required(.:format)' => 'user_sessions#login_required', :as => :login_required
  
  resources :sparql_endpoint do
    collection do
      get :cmpd_name_lookup
      get :target_name_lookup
      get :concept_name_lookup
      post :query
      post :similar2smiles
      post :search_by_smiles
      post :cmpd_by_name
      post :target_by_name
      post :pharm_by_cmpd_name
      post :pharm_by_target_name
      post :pharm_enzyme_fam
      post :concept_object_summery
      post :concept_subject_summery
      post :settings
    end  
  end
  
  resources :enzymes do
     collection do
       get :index
     end
  end
  
  root :to => "home#index"
  
  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with "root"
  # just remember to delete public/index.html.
  # root :to => "welcome#index"  

  # See how all your routes lay out with "rake routes"

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  # match ':controller(/:action(/:id(.:format)))'
end
