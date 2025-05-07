import { ResponseData } from './utils.type'

export interface RatingDTO {
   id?: number
   userId?: number
   productId?: number
   rating: number
   comment: string
   createAt?: string
   userName?: string
   userPicture?: string
   parentId?: number
   replies?: RatingDTO[]
}

export interface ReplyDTO {
   comment: string
}

export interface RatingDistribution {
   [key: number]: number
}

export interface CreateRatingParams {
   userId: number
   productId: number
   rating: RatingDTO
}

export interface ReplyRatingParams {
   userId: number
   parentRatingId: number
   reply: ReplyDTO
}

export type RatingResponse = ResponseData<RatingDTO>
export type RatingsResponse = ResponseData<RatingDTO[]>
export type AverageRatingResponse = ResponseData<number>
export type RatingCountResponse = ResponseData<number>
export type RatingDistributionResponse = ResponseData<RatingDistribution>
