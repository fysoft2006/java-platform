package com.whenling.core.support.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

import com.google.common.base.Objects;
import com.whenling.core.support.entity.Node;
import com.whenling.core.support.entity.Tree;
import com.whenling.core.support.entity.TreeEntity;
import com.whenling.core.support.entity.TreeImpl;

public class TreeRepositoryImpl<T extends TreeEntity<I, ?, T>, I extends Serializable> extends BaseRepositoryImpl<T, I>
		implements TreeRepository<T, I> {

	public TreeRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	public TreeRepositoryImpl(Class<T> domainClass, EntityManager em) {
		this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
	}

	@Override
	public List<T> findRoots() {
		return getQuery(new ParentSpecification<T>(null), (Sort) null).getResultList();
	}

	@Override
	public List<T> findAllChildren(T current) {
		return getQuery(new SublevelSpecification<T>(current), (Sort) null).getResultList();
	}

	@Override
	public Tree<T> findTree(T current) {
		List<T> allChildren = current == null ? findAll() : findAllChildren(current);
		List<Node<T>> directSubordinates = findDirectSubordinates(current, allChildren);
		if (current != null) {
			Node<T> root = toNode(current, directSubordinates);
			directSubordinates = new ArrayList<>();
			directSubordinates.add(root);
		}
		return new TreeImpl<>(directSubordinates);
	}

	protected List<Node<T>> findDirectSubordinates(T root, List<T> allChildren) {
		List<Node<T>> nodes = new ArrayList<>();
		for (T entity : allChildren) {
			if (Objects.equal(entity.getParent(), root)) {
				nodes.add(toNode(entity, findDirectSubordinates(entity, allChildren)));
			}
		}
		return nodes;
	}

	protected Node<T> toNode(T entity, List<Node<T>> children) {
		Node<T> node = new Node<>();
		node.setData(entity);
		node.setChildren(children);
		return node;
	}
}