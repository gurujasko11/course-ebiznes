package models

case class ProductOption(
                    product_option_id: Long,
                    product_id: Long,
                    option_id: Long,
                    option_group_id: Long
                  )