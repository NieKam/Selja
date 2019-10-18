import * as adItemApi from '../../helpers/adItemApi'
import AdItem from '../../components/AdItem'
import React, { Component } from 'react';

class AdItemsList extends Component {
    componentDidMount = async () => {
        const adItems = await adItemApi.getAll(52.66, -8.55)
        this.setState({ adItems })
    }

    static defaultProps = {
        adItems: []
    }

    state = {
        adItems: this.props.adItems
    }

    handleClick = (id) => {
        this.props.history.push(/items/ + id);
    }

    render() {
        const { adItems } = this.state

        return (
            <div>
                {adItems.map(item => <AdItem key={item.id} ad={item} onClick={this.handleClick} />)}
            </div>
        )
    }
}

export default AdItemsList