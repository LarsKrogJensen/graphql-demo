package se.six.lars.schema


import se.six.lars.ApiUser
import se.six.lars.IApiController
import se.six.lars.ISearchController
import se.six.lars.SearchController

data class ApiRequestContext(val user: ApiUser,
                             val apiController: IApiController,
                             val searchController: ISearchController)

