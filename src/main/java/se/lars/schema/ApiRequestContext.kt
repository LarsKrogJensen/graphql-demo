package se.lars.schema


import se.lars.ApiUser
import se.lars.IApiController
import se.lars.ISearchController
import se.lars.SearchController

data class ApiRequestContext(val user: ApiUser,
                             val apiController: IApiController,
                             val searchController: ISearchController)

