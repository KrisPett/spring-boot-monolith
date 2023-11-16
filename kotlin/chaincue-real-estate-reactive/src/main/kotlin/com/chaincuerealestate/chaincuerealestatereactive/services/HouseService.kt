package com.chaincuerealestate.chaincuerealestatereactive.services

import com.chaincuerealestate.chaincuerealestatereactive.domains.House
import com.chaincuerealestate.chaincuerealestatereactive.exceptions.HouseNotFoundException
import com.chaincuerealestate.chaincuerealestatereactive.repositories.HouseImageRepository
import com.chaincuerealestate.chaincuerealestatereactive.repositories.HouseImagesRepository
import com.chaincuerealestate.chaincuerealestatereactive.repositories.HouseRepository
import com.chaincuerealestate.chaincuerealestatereactive.utilities.AweS3Urls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class HouseService(
    private val houseRepository: HouseRepository,
    private val houseImagesRepository: HouseImagesRepository,
    private val houseImageRepository: HouseImageRepository
) : HouseServiceI {

    override suspend fun save(houseTypes: House.HouseTypes): House {
        val house = House.create(houseTypes, AweS3Urls.URLFrontImage1)
        return houseRepository.save(house)
    }

    override suspend fun findByIdWithRelations(id: String): House {
        return houseRepository.findById(id)?.apply {
            val images = houseImagesRepository.findAllByHouseId(id)
                .mapNotNull { houseImageRepository.findById(it.imageId) }
                .toList()
                .apply {
                    this.images = images
                }
            this.images = images
        } ?: throw HouseNotFoundException(id)
    }

    override suspend fun findAll(): Flow<House> {
        return houseRepository.findAll()
    }
}

