package com.oggo.auction.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.oggo.auction.model.Products;
import com.oggo.auction.repository.ProdRepository;

@Service
public class ProdService {

	@Autowired
	private ProdRepository repository;

	public List<Products> prodCheck() {
		List<Products> prodList = repository.findAllByOrderByProdIdxDesc();
		return prodList;
	}

	public List<Products> findUserBidItems(String buyerId) {
		List<Products> bidItems = repository.findUserBidItems(buyerId);
		bidItems.forEach(product -> {
			String sellerNickname = repository.findSellerNicknameByUserId(product.getUserId());
			System.out.println("Product ID: " + product.getProdIdx() + ", Seller ID: " + product.getUserId()
					+ ", Seller Nickname: " + sellerNickname);
			if (sellerNickname != null) {
				product.setSellerNickname(sellerNickname);
			} else {
				product.setSellerNickname("알 수 없음");
			}
		});
		return bidItems;
	}

	public Products prodDetail(int prodIdx) {

		Products prodInfo = repository.findByProdIdx(prodIdx);
		prodInfo.setSellerNickname(repository.findSellerNicknameByUserId(prodInfo.getUserId()));

		return prodInfo;
	}

	public void saveProduct(Products product) {
		repository.save(product);
	}

	public List<Products> myProd(String userId) {

		List<Products> myProdList = repository.findAllByUserIdOrderByProdIdxDesc(userId);

		return myProdList;
	}

	public void bid(String prodIdx, String money, String userId) {

		int intProdIdx = Integer.parseInt(prodIdx);
		int intMoney = Integer.parseInt(money);

		repository.bidUpdate(intProdIdx, intMoney, userId);
	}

	public void buy(String prodIdx, String money, String userId) {

		int intProdIdx = Integer.parseInt(prodIdx);
		int intMoney = Integer.parseInt(money);

		repository.buy(intProdIdx, intMoney, userId);
	}

}
