import { withRouter } from 'react-router-dom';
import * as adItemApi from '../../helpers/adItemApi'
import AdItem from '../../components/AdItem'
import Loading from '../../components/Loading';
import React, { Component } from 'react';

class AdItemsList extends Component {
    constructor(props) {
        super(props);
        this._isMounted = false;
    }

    componentWillUnmount() {
        this._isMounted = false;
    }

    componentDidMount = async () => {
        this._isMounted = true;
        const { lat, long } = this.props
        const adItems = await adItemApi.getAll(lat, long)
        this._isMounted && this.setState({ adItems, fetched: true })
    }

    static defaultProps = {
        adItems: [],
        lat: null,
        long: null
    }

    state = {
        adItems: this.props.adItems,
        fetched: false,
    }

    handleClick = (id) => {
        this.props.history.push(/items/ + id);
    }

    render() {
        const { adItems } = this.state

        return (
            <div>
                {
                    this.state.fetched ?
                        <div>
                            {adItems.map(item => <AdItem key={item.id} ad={item} onClick={this.handleClick} />)}
                        </div>
                        : <Loading />
                }
            </div>

        )
    }
}

export default withRouter(AdItemsList)
