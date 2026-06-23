package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

interface BlueprintJpaRepository extends JpaRepository<Blueprint, Long> {
    Optional<Blueprint> findByAuthorAndName(String author, String name);
    List<Blueprint> findByAuthor(String author);
}

@Primary
@Repository
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final BlueprintJpaRepository repo;

    public PostgresBlueprintPersistence(BlueprintJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (repo.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent())
            throw new BlueprintPersistenceException("Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName());
        repo.save(bp);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return repo.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<Blueprint> list = repo.findByAuthor(author);
        if (list.isEmpty()) throw new BlueprintNotFoundException("No blueprints for author: " + author);
        return new HashSet<>(list);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(repo.findAll());
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
        repo.save(bp);
    }
}