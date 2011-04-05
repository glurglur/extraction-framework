package org.dbpedia.extraction.mappings

import org.dbpedia.extraction.destinations.{DBpediaDatasets, Graph, Quad, IriRef}
import org.dbpedia.extraction.ontology.OntologyNamespaces
import org.dbpedia.extraction.wikiparser._

/**
 * Extracts redirect links between Articles in Wikipedia.
 */
class RedirectExtractor(extractionContext : ExtractionContext) extends Extractor
{
    val wikiPageRedirectsProperty = extractionContext.ontology.getProperty("wikiPageRedirects")
                                   .getOrElse(throw new NoSuchElementException("Ontology property 'wikiPageRedirects' does not exist in DBpedia Ontology."))

    override def extract(page : PageNode, subjectUri : String, pageContext : PageContext) : Graph =
    {
        if(page.title.namespace == WikiTitle.Namespace.Main && page.isRedirect)
        {
            for(destination <- page.children.collect{case InternalLinkNode(destination, _, _, _) => destination})
            {
                return new Graph(new Quad(DBpediaDatasets.Redirects, new IriRef(subjectUri), new IriRef(wikiPageRedirectsProperty),
                    new IriRef(OntologyNamespaces.DBPEDIA_INSTANCE_NAMESPACE + destination.encodedWithNamespace), new IriRef(page.sourceUri)))
            }
        }

        new Graph()
    }
}
